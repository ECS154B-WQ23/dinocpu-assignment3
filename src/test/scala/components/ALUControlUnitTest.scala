// Unit tests for the ALU control logic

package dinocpu.test.components

import chisel3._
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import dinocpu.components.ALUControl

class ALUControlUnitTester(c: ALUControl) extends PeekPokeTester(c) {
  private val ctl = c

  // Copied from Patterson and Waterman Figure 2.3
  val tests = List(
    // insttype,       Funct7,    Func3,   Control Input
    (       5.U, "b0000000".U, "b000".U, "b01100".U, "load/store"),
    (       5.U, "b1111111".U, "b111".U, "b01100".U, "load/store"),
    (       5.U, "b0000000".U, "b000".U, "b01100".U, "load/store"),
    (       1.U, "b0000000".U, "b000".U, "b01100".U, "add"),
    (       1.U, "b0100000".U, "b000".U, "b01110".U, "sub"),
    (       1.U, "b0000000".U, "b001".U, "b00110".U, "sll"),
    (       1.U, "b0000000".U, "b010".U, "b01010".U, "slt"),
    (       1.U, "b0000000".U, "b011".U, "b01011".U, "sltu"),
    (       1.U, "b0000000".U, "b100".U, "b00010".U, "xor"),
    (       1.U, "b0000000".U, "b101".U, "b01000".U, "srl"),
    (       1.U, "b0100000".U, "b101".U, "b00100".U, "sra"),
    (       1.U, "b0000000".U, "b110".U, "b00001".U, "or"),
    (       1.U, "b0000000".U, "b111".U, "b00000".U, "and"),
    (       1.U, "b0000001".U, "b000".U, "b10000".U, "mul"),
    (       1.U, "b0000001".U, "b001".U, "b10010".U, "mulh"),
    (       1.U, "b0000001".U, "b010".U, "b11100".U, "mulhsu"),
    (       1.U, "b0000001".U, "b011".U, "b10011".U, "mulhu"),
    (       1.U, "b0000001".U, "b100".U, "b10100".U, "div"),
    (       1.U, "b0000001".U, "b101".U, "b10101".U, "divu"),
    (       1.U, "b0000001".U, "b110".U, "b11000".U, "rem"),
    (       1.U, "b0000001".U, "b111".U, "b11001".U, "remu"),
    (       3.U, "b0000000".U, "b000".U, "b01101".U, "addw"),
    (       3.U, "b0100000".U, "b000".U, "b01111".U, "subw"),
    (       3.U, "b0000000".U, "b001".U, "b00111".U, "sllw"),
    (       3.U, "b0000000".U, "b101".U, "b01001".U, "srlw"),
    (       3.U, "b0100000".U, "b101".U, "b00101".U, "sraw"),
    (       3.U, "b0000001".U, "b000".U, "b10001".U, "mulw"),
    (       3.U, "b0000001".U, "b100".U, "b10110".U, "divw"),
    (       3.U, "b0000001".U, "b101".U, "b10111".U, "divuw"),
    (       3.U, "b0000001".U, "b110".U, "b11010".U, "remw"),
    (       3.U, "b0000001".U, "b111".U, "b11011".U, "remuw"),
    (       2.U, "b0000000".U, "b000".U, "b01100".U, "addi"),
    (       2.U, "b0000000".U, "b010".U, "b01010".U, "slti"),
    (       2.U, "b0000000".U, "b011".U, "b01011".U, "sltiu"),
    (       2.U, "b0000000".U, "b100".U, "b00010".U, "xori"),
    (       2.U, "b0000000".U, "b110".U, "b00001".U, "ori"),
    (       2.U, "b0000000".U, "b111".U, "b00000".U, "andi"),
    (       2.U, "b0000000".U, "b001".U, "b00110".U, "slli"),
    (       2.U, "b0000000".U, "b101".U, "b01000".U, "srli"),
    (       2.U, "b0100000".U, "b101".U, "b00100".U, "srai"),
    (       4.U, "b0000000".U, "b000".U, "b01101".U, "addiw"),
    (       4.U, "b0000000".U, "b001".U, "b00111".U, "slliw"),
    (       4.U, "b0000000".U, "b101".U, "b01001".U, "srliw"),
    (       4.U, "b0100000".U, "b101".U, "b00101".U, "sraiw"),
  )
  for (t <- tests) {
    poke(ctl.io.aluop, t._1)
    poke(ctl.io.funct7, t._2)
    poke(ctl.io.funct3, t._3)
    step(1)
    expect(ctl.io.operation, t._4, s"${t._5} wrong")
  }
}

/**
  * This is a trivial example of how to run this Specification
  * From within sbt use:
  * {{{
  * testOnly dinocpu.ALUControlTester
  * }}}
  * From a terminal shell use:
  * {{{
  * sbt 'testOnly dinocpu.ALUControlTester'
  * }}}
  */
class ALUControlTester extends ChiselFlatSpec {
  "ALUControl" should s"match expectations for each intruction type" in {
    Driver(() => new ALUControl) {
      c => new ALUControlUnitTester(c)
    } should be (true)
  }
}
