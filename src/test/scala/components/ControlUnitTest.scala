// Unit tests for the main control logic

package dinocpu.test.components

import chisel3._
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import dinocpu.components.Control

class ControlUnitTester(c: Control) extends PeekPokeTester(c) {
  private val ctl = c

  val tests = List(
    // Inputs,        aluop, controltransferop, memop, op1_src, op2_src, writeback_valid, writeback_src, validinst
    ( "b0110011".U,     1.U,               0.U,   0.U,     0.U,     0.U,             1.U,           0.U,       1.U), // R-format 64-bit operands
    ( "b0010011".U,     2.U,               0.U,   0.U,     0.U,     2.U,             1.U,           0.U,       1.U), // I-format 64-bit operands
    ( "b0111011".U,     3.U,               0.U,   0.U,     0.U,     0.U,             1.U,           0.U,       1.U), // R-format 32-bit operands
    ( "b0011011".U,     4.U,               0.U,   0.U,     0.U,     2.U,             1.U,           0.U,       1.U), // I-format 32-bit operands
    ( "b0000011".U,     5.U,               0.U,   1.U,     0.U,     2.U,             1.U,           2.U,       1.U), // load
    ( "b0100011".U,     5.U,               0.U,   2.U,     0.U,     2.U,             0.U,           0.U,       1.U), // store
    ( "b1100011".U,     0.U,               3.U,   0.U,     0.U,     0.U,             0.U,           0.U,       1.U), // branch
    ( "b0110111".U,     0.U,               0.U,   0.U,     0.U,     0.U,             1.U,           1.U,       1.U), // lui
    ( "b0010111".U,     5.U,               0.U,   0.U,     1.U,     2.U,             1.U,           0.U,       1.U), // auipc
    ( "b1101111".U,     5.U,               1.U,   0.U,     1.U,     1.U,             1.U,           0.U,       1.U), // jal
    ( "b1100111".U,     5.U,               2.U,   0.U,     1.U,     1.U,             1.U,           0.U,       1.U)  // jalr
  )
                      
  for (t <- tests) {
    poke(ctl.io.opcode, t._1)
    step(1)
    expect(ctl.io.aluop, t._2)
    expect(ctl.io.controltransferop, t._3)
    expect(ctl.io.memop, t._4)
    expect(ctl.io.op1_src, t._5)
    expect(ctl.io.op2_src, t._6)
    expect(ctl.io.writeback_valid, t._7)
    expect(ctl.io.writeback_src, t._8)
    expect(ctl.io.validinst, t._9)
  }
}

/**
  * This is a trivial example of how to run this Specification
  * From within sbt use:
  * {{{
  * testOnly dinocpu.test.ControlTester
  * }}}
  * From a terminal shell use:
  * {{{
  * sbt 'testOnly dinocpu.test.ControlTester'
  * }}}
  */
class ControlTester extends ChiselFlatSpec {
  "Control" should s"match expectations" in {
    Driver(() => new Control) {
      c => new ControlUnitTester(c)
    } should be (true)
  }
}
