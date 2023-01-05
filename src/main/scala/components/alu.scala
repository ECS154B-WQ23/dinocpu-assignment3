// This file contains the ALU logic and the ALU control logic.

package dinocpu.components

import chisel3._
import chisel3.util._

/**
 * The ALU
 *
 * Input:  operation, specifies which operation the ALU should perform
 * Input:  operand1, the first input (e.g., reg1)
 * Input:  operand2, the second input (e.g., reg2)
 * Output: the result of the computation
 */
class ALU extends Module {
  val io = IO(new Bundle {
    val operation = Input(UInt(5.W))
    val operand1    = Input(UInt(64.W))
    val operand2    = Input(UInt(64.W))

    val result    = Output(UInt(64.W))
  })

  def mulhu_helper(op1: UInt, op2: UInt) : UInt = {
    // operand1 = a << 32 + b
    // operand2 = c << 32 + d
    // operand1 * operand2 = (ac << 64) + ad << 32 + bc << 32 + bd
    // (operand1 * operand2) >> 64 = ac + (ad >> 32) + (bc >> 32) + (ad % (2**32) + bc % (2**32) + bd >> 32) // 2**32
    //                               |--------------------------|   |--------------------------------------|
    //                                          alpha                                beta
    val a = op1(63, 32).asUInt
    val b = op1(31, 0).asUInt
    val c = op2(63, 32).asUInt
    val d = op2(31, 0).asUInt
    val alpha = Wire(UInt(64.W))
    alpha := a*c + ((a*d) >> 32) + ((b*c) >> 32)
    val beta = Wire(UInt(64.W))
    beta := ((a*d) % (1.U << 32)) + (b*c) % (1.U << 32) + ((b*d) >> 32)
    val result = Wire(UInt(64.W))
    result := alpha + (beta >> 32)
    return result
  }

  // this function casts the input to 32-bit UInt, then sign extends it
  val signExtend32To64 = (input: UInt) => Cat(Fill(32, input(31)), input(31, 0))
  val operand1_32 = io.operand1(31, 0)
  val operand2_32 = io.operand2(31, 0)

  io.result := 0.U

  when (io.operation === "b00000".U) { // and
    io.result := io.operand1 & io.operand2
  }
  .elsewhen (io.operation === "b00001".U) { // or
    io.result := io.operand1 | io.operand2
  }
  .elsewhen (io.operation === "b00010".U) { // xor
    io.result := io.operand1 ^ io.operand2
  }
  .elsewhen (io.operation === "b00100".U) { // sra
    io.result := (io.operand1.asSInt >> io.operand2(5, 0)).asUInt // sra uses 6 bits of op2
  }
  .elsewhen (io.operation === "b00101".U) { // sraw
    io.result := signExtend32To64((operand1_32.asSInt >> operand2_32(4, 0)).asUInt) // arithmetic (signed)
                                                                                // sraw takes 5 bits of op2
  }
  .elsewhen (io.operation === "b00110".U) { // sll
    io.result := io.operand1 << io.operand2(5, 0) // sll uses 6 bits of op2
  }
  .elsewhen (io.operation === "b00111".U) { // sllw
    io.result := signExtend32To64(operand1_32 << operand2_32(4, 0)) // sllw uses 5 bits of op2
  }
  .elsewhen (io.operation === "b01000".U) { // srl
    io.result := io.operand1 >> io.operand2(5, 0) // srl uses 6 bits of op2
  }
  .elsewhen (io.operation === "b01001".U) { // srlw
    io.result := signExtend32To64(operand1_32 >> operand2_32(4, 0)) // srlw uses 5 bits of op2
  }
  .elsewhen (io.operation === "b01010".U) { // slt
    io.result := (io.operand1.asSInt < io.operand2.asSInt).asUInt // signed operands
  }
  .elsewhen (io.operation === "b01011".U) { // sltu
    io.result := (io.operand1.asUInt < io.operand2.asUInt).asUInt
  }
  .elsewhen (io.operation === "b01100".U) { // add
    io.result := io.operand1 + io.operand2
  }
  .elsewhen (io.operation === "b01101".U) { // addw
    io.result := signExtend32To64(operand1_32 + operand2_32) // + results in width of max(width(op1), width(op2))
  }
  .elsewhen (io.operation === "b01110".U) { // sub
    io.result := io.operand1 - io.operand2
  }
  .elsewhen (io.operation === "b01111".U) { // subw
    io.result := signExtend32To64(operand1_32 - operand2_32)
  }
  .elsewhen (io.operation === "b10000".U) { // mul
    io.result := io.operand1 * io.operand2
  }
  .elsewhen (io.operation === "b10001".U) { // mulw
    val mul_result = Wire(SInt(32.W))
    mul_result := operand1_32.asSInt * operand2_32.asSInt
    io.result := signExtend32To64(mul_result.asUInt)
  }
  .elsewhen (io.operation === "b10010".U) { // mulh
    // The longer version
    when ((io.operand1 === 0.U) || (io.operand2 === 0.U)) {
      io.result := 0.U
    }
    .otherwise {
      val result = mulhu_helper(io.operand1.asSInt.abs().asUInt, io.operand2.asSInt.abs().asUInt)
      when (((io.operand1.asSInt > 0.S) && (io.operand2.asSInt > 0.S)) || (((io.operand1.asSInt <= 0.S) && (io.operand2.asSInt <= 0.S)))) {
        io.result := result
      }
      .otherwise {
        io.result := (-(result + 1.U)).asUInt
      }
    }
    // The shorter version
    //val temp = Wire(SInt(128.W))
    //temp := io.operand1.asSInt * io.operand2.asSInt
    //io.result := temp(127, 64)
  }
  .elsewhen (io.operation === "b10011".U) { // mulhu
    io.result := mulhu_helper(io.operand1, io.operand2)
    // The shorter version
    //val temp = Wire(UInt(128.W))
    //temp := io.operand1.asUInt * io.operand2.asUInt
    //io.result := temp(127, 64)
  }
  .elsewhen (io.operation === "b10100".U) { // div
    when (io.operand2 === 0.U) { // division by zero
      io.result := (-1).S(64.W).asUInt
    }
    .elsewhen ((io.operand1.asSInt === (-1).S(64.W) << 63) && (io.operand2.asSInt === -1.S)) { // overflow
      io.result := io.operand1
    }
    .otherwise { // rounding towards zero, i.e., negate the equivalent unsigned division and put the approriate sign
                 // though, Chisel.SInt / Chisel.SInt rounds towards zero by default
      io.result := (io.operand1.asSInt / io.operand2.asSInt).asUInt
    }
  }
  .elsewhen (io.operation === "b10101".U) { // divu
    when (io.operand2 === 0.U) { // division by zero
      io.result := (-1).S(64.W).asUInt
    }
    .otherwise { // rounding towards zero
      io.result := io.operand1 / io.operand2
    }
  }
  .elsewhen (io.operation === "b10110".U) { // divw
    when (operand2_32 === 0.U) { // division by zero
      io.result := (-1).S(64.W).asUInt
    }
    .elsewhen ((operand1_32.asSInt === ((-1).S(32.W) << 31)) && (operand2_32.asSInt === -1.S)) { // overflow
      io.result := signExtend32To64(operand1_32)
    }
    .otherwise {
      io.result := signExtend32To64((operand1_32.asSInt / operand2_32.asSInt).asUInt)
    }
  }
  .elsewhen (io.operation === "b10111".U) { // divuw
    when (operand2_32 === 0.U) { // division by zero
      io.result := (-1).S(64.W).asUInt
    }
    .otherwise {
      io.result := signExtend32To64(operand1_32 / operand2_32)
    }
  }
  .elsewhen (io.operation === "b11000".U) { // rem
    when (io.operand2 === 0.U) { // division by zero
      io.result := io.operand1
    }
    .elsewhen ((io.operand1.asSInt === (((-1).S(64.W)) << 63)) && (io.operand2.asSInt === -1.S)) { // overflow
      io.result := 0.U
    }
    .otherwise { // rounding towards zero, Chisel.SInt % Chisel.SInt rounds towards zero by default
      io.result := (io.operand1.asSInt % io.operand2.asSInt).asUInt
    }
  }
  .elsewhen (io.operation === "b11001".U) { // remu
    when (io.operand2 === 0.U) { // division by zero
      io.result := io.operand1
    }
    .otherwise {
      io.result := io.operand1 % io.operand2
    }
  }
  .elsewhen (io.operation === "b11010".U) { // remw
    when (operand2_32 === 0.U) { // division by zero
      io.result := signExtend32To64(operand1_32)
    }
    .elsewhen ((operand1_32.asSInt === (((-1).S(32.W)) << 31)) && (operand2_32.asSInt === -1.S)) { // overflow
      io.result := 0.U
    }
    .otherwise {
      io.result := signExtend32To64((operand1_32.asSInt % operand2_32.asSInt).asUInt)
    }
  }
  .elsewhen (io.operation === "b11011".U) { // remuw
    when (io.operand2 === 0.U) { // division by zero
      io.result := signExtend32To64(operand1_32)
    }
    .otherwise {
      io.result := signExtend32To64(operand1_32 % operand2_32)
    }
  }
  .elsewhen (io.operation === "b11100".U) { // mulhsu
    // The longer version
    when ((io.operand1 === 0.U) || (io.operand2 === 0.U)) {
      io.result := 0.U
    }
    .otherwise {
      val result = mulhu_helper(io.operand1.asSInt.abs().asUInt, io.operand2)
      when (io.operand1.asSInt >= 0.S) {
        io.result := result
      }
      .otherwise {
        io.result := (-(result + 1.U)).asUInt
      }
    }
    // The shorter version
    //val temp = Wire(UInt(128.W))
    //temp := io.operand1.asSInt * io.operand2.asUInt
    //io.result := temp(127, 64)
  }
  .otherwise {
    io.result := 0.U // should be invalid
  }
}
