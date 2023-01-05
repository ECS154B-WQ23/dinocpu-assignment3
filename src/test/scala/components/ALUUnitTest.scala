// Unit tests for the ALU

package dinocpu.test.components

import chisel3._
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import dinocpu.components.ALU


class ALURandomUnitTester(c: ALU) extends PeekPokeTester(c) {
  private val alu = c

  val maxInt = BigInt("FFFFFFFFFFFFFFFF", 16)

  def test(op: UInt, f: (BigInt, BigInt) => BigInt) {
    for (i <- 0 until 10) {
      val x = rnd.nextInt(100000000)
      val y = rnd.nextInt(500000000)
      poke(alu.io.operation, op)
      poke(alu.io.operand1, x)
      poke(alu.io.operand2, y)
      step(1)
      val expectOut = f(x, y).toLong & maxInt
      expect(alu.io.result, expectOut, s"for operation ${op.toInt.toBinaryString}; inputx: ${x}; inputy: ${y}")
    }
  }

  def twoscomp(v: BigInt) : BigInt = {
    if (v < 0) {
      return maxInt + v + 1
    } else {
      return v
    }
  }

  def to32bit(v: BigInt) : BigInt = {
    return v & BigInt("FFFFFFFF", 16)
  }

  def signExtend32bitTo64bit(v: BigInt) : BigInt = {
    val signBit = (v >> 31) & 1
    val bitMask32 = BigInt("FFFFFFFF", 16)
    if (signBit == 0) {
      return v & bitMask32 // we only keep the lower half since the upper half must be all zeros
    } else {
      return v | (bitMask32 << 32) // the upper half must be all ones, the lower half must be preserved
    }
  }

  test("b00000".U, (x: BigInt, y: BigInt) => (x & y)) // and
  test("b00001".U, (x: BigInt, y: BigInt) => (x | y)) // or
  test("b00010".U, (x: BigInt, y: BigInt) => (x ^ y)) // xor
  test("b00100".U, (x: BigInt, y: BigInt) => (x >> (y.toInt & 0x3f))) // sra
  test("b00101".U, (x: BigInt, y: BigInt) => (signExtend32bitTo64bit(to32bit(x) >> (y.toInt & 0x1f)))) // sraw
  test("b00110".U, (x: BigInt, y: BigInt) => (x << (y.toInt & 0x3f))) // sll
  test("b00111".U, (x: BigInt, y: BigInt) => (signExtend32bitTo64bit(to32bit(x) << (y.toInt & 0x1f)))) // sllw
  test("b01000".U, (x: BigInt, y: BigInt) => (x >> (y.toInt & 0x3f))) // srl
  test("b01001".U, (x: BigInt, y: BigInt) => (signExtend32bitTo64bit(to32bit(x) >> (y.toInt & 0x1f)))) // srlw
  test("b01010".U, (x: BigInt, y: BigInt) => (if (x < y) 1 else 0)) // slt
  test("b01011".U, (x: BigInt, y: BigInt) => (if (x < y) 1 else 0)) // sltu
  test("b01100".U, (x: BigInt, y: BigInt) => (x+y)) // add
  test("b01101".U, (x: BigInt, y: BigInt) => (signExtend32bitTo64bit(to32bit(x) + to32bit(y)))) // addw
  test("b01110".U, (x: BigInt, y: BigInt) => twoscomp(x - y)) // sub
  test("b01111".U, (x: BigInt, y: BigInt) => (signExtend32bitTo64bit(twoscomp(to32bit(x) - to32bit(y))))) // subw
  test("b10000".U, (x: BigInt, y: BigInt) => (x*y) mod (BigInt(2) pow 64)) // mul
  test("b10001".U, (x: BigInt, y: BigInt) => ((to32bit(x) * to32bit(y))) mod (BigInt(2) pow 32)) // mulw
  test("b10010".U, (x: BigInt, y: BigInt) => (x*y) / (BigInt(2) pow 64)) // mulh
  test("b10011".U, (x: BigInt, y: BigInt) => (x*y) / (BigInt(2) pow 64)) // mulhu
  test("b10100".U, (x: BigInt, y: BigInt) => (x/y)) // div
  test("b10101".U, (x: BigInt, y: BigInt) => (x/y)) // divu
  test("b10110".U, (x: BigInt, y: BigInt) => (to32bit(x)/to32bit(y))) // divw
  test("b10111".U, (x: BigInt, y: BigInt) => (to32bit(x)/to32bit(y))) // divuw
  test("b11000".U, (x: BigInt, y: BigInt) => (x mod y)) // rem
  test("b11001".U, (x: BigInt, y: BigInt) => (x mod y)) // remu
  test("b11010".U, (x: BigInt, y: BigInt) => (to32bit(x) mod to32bit(y))) // remw
  test("b11011".U, (x: BigInt, y: BigInt) => (to32bit(x) mod to32bit(y))) // remuw
  test("b11100".U, (x: BigInt, y: BigInt) => (x*y) / (BigInt(2) pow 64)) // mulhsu
}

class ALUDirectedUnitTester(c: ALU) extends PeekPokeTester(c) {
  private val alu = c
  val maxInt = BigInt("FFFFFFFFFFFFFFFF", 16)

  def twoscomp(v: BigInt) : BigInt = {
    if (v < 0) {
      return maxInt + v + 1
    } else {
      return v
    }
  }

  // signed <
  poke(alu.io.operation, "b01010".U)
  poke(alu.io.operand1, twoscomp(-1))
  poke(alu.io.operand2, 1)
  step(1)
  expect(alu.io.result, 1)

  // unsigned <
  poke(alu.io.operation, "b01011".U)
  poke(alu.io.operand1, maxInt)
  poke(alu.io.operand2, 1)
  step(1)
  expect(alu.io.result, 0)

  // signed >>
  poke(alu.io.operation, "b00100".U)
  poke(alu.io.operand1, twoscomp(-1024))
  poke(alu.io.operand2, 5)
  step(1)
  expect(alu.io.result, twoscomp(-32))
}

/**
  * This is a trivial example of how to run this Specification
  * From within sbt use:
  * {{{
  * testOnly dinocpu.ALUTester
  * }}}
  * From a terminal shell use:
  * {{{
  * sbt 'testOnly dinocpu.ALUTester'
  * }}}
  */
class ALUTester extends ChiselFlatSpec {
  "ALU" should s"match expectations for random tests" in {
    Driver(() => new ALU) {
      c => new ALURandomUnitTester(c)
    } should be (true)
  }
  "ALU" should s"match expectations for directed edge tests" in {
    Driver(() => new ALU) {
      c => new ALUDirectedUnitTester(c)
    } should be (true)
  }
}
