// Control logic for the processor

package dinocpu.components

import chisel3._
import chisel3.util.{BitPat, ListLookup}

/**
 * Main control logic for our simple processor
 *
 * Input: opcode:                Opcode from instruction
 *
 * Output: aluop                 Specifying the type of instruction using ALU
 *                                   . 0 for none of the below
 *                                   . 1 for 64-bit R-type
 *                                   . 2 for 64-bit I-type
 *                                   . 3 for 32-bit R-type
 *                                   . 4 for 32-bit I-type
 *                                   . 5 for non-arithmetic instruction types that uses ALU (auipc/jal/jarl/Load/Store)
 * Output: controltransferop     Specifying the type of control transfer instruction (J-type/B-type)
 *                                   . 0 for none of the below
 *                                   . 1 for jal
 *                                   . 2 for jalr
 *                                   . 3 for branch instructions (B-type)
 * Output: memop                 Specifying the type of memory instruction (Load/Store)
 *                                   . 0 for none of the below
 *                                   . 1 for Load
 *                                   . 2 for Store
 * Output: op1_src               Specifying the source of operand1 of ALU/ControlTransferUnit
 *                                   . 0 if source is register file's readdata1
 *                                   . 1 if source is pc
 * Output: op2_src               Specifying the source of operand2 of ALU/ControlTransferUnit
 *                                   . 0 if source is register file's readdata2
 *                                   . 1 if source is a hardwired value 4
 *                                   . 2 if source is immediate
 * Output: writeback_valid       0 if not writing back to registers, 1 otherwise
 * Output: writeback_src         Specifying the source of value written back to the register file
 *                                   . 0 to select alu result
 *                                   . 1 to select immediate generator result
 *                                   . 2 to select data memory result
 * Output: validinst             0 if the instruction is invalid, 1 otherwise
 *
 * For more information, see section 4.4 of Patterson and Hennessy.
 * This follows figure 4.22.
 */

class Control extends Module {
  val io = IO(new Bundle {
    val opcode = Input(UInt(7.W))

    val aluop             = Output(UInt(3.W))
    val controltransferop = Output(UInt(2.W))
    val memop             = Output(UInt(2.W))
    val op1_src           = Output(UInt(1.W))
    val op2_src           = Output(UInt(2.W))
    val writeback_valid   = Output(UInt(1.W))
    val writeback_src     = Output(UInt(2.W))
    val validinst         = Output(UInt(1.W))
  })

  val signals =
    ListLookup(io.opcode,
      /*default*/           List(     0.U,               0.U,   0.U,     0.U,     0.U,             0.U,           0.U,       0.U),
      Array(              /*        aluop, controltransferop, memop, op1_src, op2_src, writeback_valid, writeback_src, validinst*/
      // R-format 64-bit operands
      BitPat("b0110011") -> List(     1.U,               0.U,   0.U,     0.U,     0.U,             1.U,           0.U,       1.U),
      // R-format 32-bit operands
      BitPat("b0111011") -> List(     3.U,               0.U,   0.U,     0.U,     0.U,             1.U,           0.U,       1.U),
      ) // Array
    ) // ListLookup

  io.aluop             := signals(0)
  io.controltransferop := signals(1)
  io.memop             := signals(2)
  io.op1_src           := signals(3)
  io.op2_src           := signals(4)
  io.writeback_valid   := signals(5)
  io.writeback_src     := signals(6)
  io.validinst         := signals(7)
}
