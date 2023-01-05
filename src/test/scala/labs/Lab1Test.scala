// Tests for Lab 1. Feel free to modify and add more tests here.
// If you name your test class something that ends with "TesterLab1" it will
// automatically be run when you use `Lab1 / test` at the sbt prompt.

package dinocpu

import dinocpu._
import dinocpu.components._
import dinocpu.test._
import chisel3._
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}

class ALUControlUnitRTypeTester(c: ALUControl) extends PeekPokeTester(c) {
  private val ctl = c

  // Copied from Patterson and Waterman Figure 2.3
  val tests = List(
    //    aluop,       Funct7,    Func3,   Control Input
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
  * Lab1 / testOnly dinocpu.test.ALUControlTesterLab1
  * }}}
  * From a terminal shell use:
  * {{{
  * sbt 'Lab1 / testOnly dinocpu.test.ALUControlTesterLab1'
  * }}}
  */
class ALUControlTesterLab1 extends ChiselFlatSpec {
  "ALUControl" should s"match expectations for each intruction type" in {
    Driver(() => new ALUControl) {
      c => new ALUControlUnitRTypeTester(c)
    } should be (true)
  }
}

/**
  * This is a trivial example of how to run this Specification
  * From within sbt use:
  * {{{
  * Lab1 / testOnly dinocpu.SingleCycleAddTesterLab1
  * }}}
  * From a terminal shell use:
  * {{{
  * sbt 'Lab1 / testOnly dinocpu.SingleCycleAddTesterLab1'
  * }}}
  */
class SingleCycleAddTesterLab1 extends CPUFlatSpec {
  behavior of "Single Cycle CPU"
  var test = InstTests.nameMap("add1")
  it should s"run add test ${test.binary}${test.extraName}" in {
    CPUTesterDriver(test, "single-cycle") should be(true)
  }
  test = InstTests.nameMap("add2")
  it should s"run add test ${test.binary}${test.extraName}" in {
    CPUTesterDriver(test, "single-cycle") should be(true)
  }
}

/**
  * This is a trivial example of how to run this Specification
  * From within sbt use:
  * {{{
  * Lab1 / testOnly dinocpu.SingleCycleAdd0TesterLab1
  * }}}
  * From a terminal shell use:
  * {{{
  * sbt 'Lab1 / testOnly dinocpu.SingleCycleAdd0TesterLab1'
  * }}}
  */
class SingleCycleAdd0TesterLab1 extends CPUFlatSpec {
  val test = InstTests.nameMap("add0")
  "Single Cycle CPU" should s"run add test ${test.binary}${test.extraName}" in {
    CPUTesterDriver(test, "single-cycle") should be(true)
  }
}

/**
  * This is a trivial example of how to run this Specification
  * From within sbt use:
  * {{{
  * Lab1 / testOnly dinocpu.SingleCycleRTypeTesterLab1
  * }}}
  * From a terminal shell use:
  * {{{
  * sbt 'Lab1 / testOnly dinocpu.SingleCycleRTypeTesterLab1'
  * }}}
  *
  * To run a **single** test from this suite, you can use the -z option to sbt test.
  * The option after the `-z` is a string to search for in the test
  * {{{
  * sbt> testOnly dinocpu.SingleCycleRTypeTesterLab1 -- -z add1
  * }}}
  * Or, to run just the r-type instructions you can use `-z rtype`
  */
class SingleCycleRTypeTesterLab1 extends CPUFlatSpec {
  behavior of "Single Cycle CPU"
  for (test <- InstTests.rtype) {
    if (test.binary != "add0" && test.binary != "add1" && test.binary != "add2") {
      it should s"run R-type instruction ${test.binary}${test.extraName}" in {
        CPUTesterDriver(test, "single-cycle") should be(true)
      }
    }
  }
}

/**
  * From within sbt use:
  * {{{
  * Lab1 / testOnly dinocpu.SingleCycleRTypeMExtensionTesterLab1
  * }}}
  * From a terminal shell use:
  * {{{
  * sbt 'Lab1 / testOnly dinocpu.SingleCycleRTypeMExtensionTesterLab1'
  * }}}
  */
class SingleCycleRTypeMExtensionTesterLab1 extends CPUFlatSpec {
  behavior of "Single Cycle CPU"
  for (test <- InstTests.rtypeMExtension) {
    it should s"run R-type instruction ${test.binary}${test.extraName}" in {
      CPUTesterDriver(test, "single-cycle") should be(true)
    }
  }
}

/**
  * This is a trivial example of how to run this Specification
  * From within sbt use:
  * {{{
  * Lab1 / testOnly dinocpu.SingleCycleMultiCycleTesterLab1
  * }}}
  * From a terminal shell use:
  * {{{
  * sbt 'Lab1 / testOnly dinocpu.SingleCycleMultiCycleTesterLab1'
  * }}}
  *
  * To run a **single** test from this suite, you can use the -z option to sbt test.
  * The option after the `-z` is a string to search for in the test
  * {{{
  * sbt> testOnly dinocpu.SingleCycleMultiCycleTesterLab1 -- -z swapxor
  * }}}
  * Or, to run just the r-type instructions you can use `-z rtype`
  */
class SingleCycleMultiCycleTesterLab1 extends CPUFlatSpec {
  behavior of "Single Cycle CPU"
  for (test <- InstTests.rtypeMultiCycle) {
    it should s"run R-type multi-cycle program ${test.binary}${test.extraName}" in {
      CPUTesterDriver(test, "single-cycle") should be(true)
    }
  }
}
