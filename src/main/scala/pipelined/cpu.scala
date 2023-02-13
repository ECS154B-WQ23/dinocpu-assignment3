// This file is where all of the CPU components are assembled into the whole CPU

package dinocpu.pipelined

import chisel3._
import chisel3.util._
import dinocpu._
import dinocpu.components._

/**
 * The main CPU definition that hooks up all of the other components.
 *
 * For more information, see section 4.6 of Patterson and Hennessy
 * This follows figure 4.49
 */
class PipelinedCPU(implicit val conf: CPUConfig) extends BaseCPU {
  // Everything in the register between IF and ID stages
  class IFIDBundle extends Bundle {
    val instruction = UInt(32.W)
    val pc          = UInt(64.W)
  }

  // Control signals used in EX stage
  class EXControl extends Bundle {
  }

  // Control signals used in MEM stage
  class MControl extends Bundle {
  }

  // Control signals used in WB stage
  class WBControl extends Bundle {
  }

  // Data of the the register between ID and EX stages
  class IDEXBundle extends Bundle {
  }

  // Control block of the IDEX register
  class IDEXControl extends Bundle {
    val ex_ctrl  = new EXControl
    val mem_ctrl = new MControl
    val wb_ctrl  = new WBControl
  }

  // Everything in the register between EX and MEM stages
  class EXMEMBundle extends Bundle {
  }

  // Control block of the EXMEM register
  class EXMEMControl extends Bundle {
    val mem_ctrl  = new MControl
    val wb_ctrl   = new WBControl
  }

  // Everything in the register between MEM and WB stages
  class MEMWBBundle extends Bundle {
  }

  // Control block of the MEMWB register
  class MEMWBControl extends Bundle {
    val wb_ctrl = new WBControl
  }

  // All of the structures required
  val pc              = RegInit(0.U(64.W))
  val control         = Module(new Control())
  val registers       = Module(new RegisterFile())
  val aluControl      = Module(new ALUControl())
  val alu             = Module(new ALU())
  val immGen          = Module(new ImmediateGenerator())
  val controlTransfer = Module(new ControlTransferUnit())
  val pcPlusFour      = Module(new Adder())
  val forwarding      = Module(new ForwardingUnit())  //pipelined only
  val hazard          = Module(new HazardUnit())      //pipelined only
  val (cycleCount, _) = Counter(true.B, 1 << 30)

  // The four pipeline registers
  val if_id       = Module(new StageReg(new IFIDBundle))

  val id_ex       = Module(new StageReg(new IDEXBundle))
  val id_ex_ctrl  = Module(new StageReg(new IDEXControl))

  val ex_mem      = Module(new StageReg(new EXMEMBundle))
  val ex_mem_ctrl = Module(new StageReg(new EXMEMControl))

  val mem_wb      = Module(new StageReg(new MEMWBBundle))

  // To make the interface of the mem_wb_ctrl register consistent with the other control
  // registers, we create an anonymous Bundle
  val mem_wb_ctrl = Module(new StageReg(new MEMWBControl))

  // Remove when connected
  control.io          := DontCare
  registers.io        := DontCare
  aluControl.io       := DontCare
  alu.io              := DontCare
  immGen.io           := DontCare
  controlTransfer.io  := DontCare
  pcPlusFour.io       := DontCare
  forwarding.io       := DontCare
  hazard.io           := DontCare

  io.dmem := DontCare
  dontTouch(pc)

  id_ex.io       := DontCare
  id_ex_ctrl.io  := DontCare
  ex_mem.io      := DontCare
  ex_mem_ctrl.io := DontCare
  mem_wb.io      := DontCare
  mem_wb_ctrl.io := DontCare

  // From memory back to fetch. Since we don't decide whether to take a branch or not until the memory stage.
  val next_pc = Wire(UInt(64.W))
  next_pc := DontCare // Remove when connected

  /////////////////////////////////////////////////////////////////////////////
  // FETCH STAGE
  /////////////////////////////////////////////////////////////////////////////

  // Update the PC:
  // (Part I) Choose between PC+4 and nextpc from the ControlTransferUnit to update PC
  // (Part III) Only update PC when pcstall is false

  // Send the PC to the instruction memory port to get the instruction
  io.imem.address := pc
  io.imem.valid   := true.B

  // Fill the IF/ID register
  when ((pc % 8.U) === 4.U) {
    if_id.io.in.instruction := io.imem.instruction(63, 32)
  } .otherwise {
    if_id.io.in.instruction := io.imem.instruction(31, 0)
  }
  if_id.io.in.pc := pc

  // Update during Part III when implementing branches/jump
  if_id.io.valid := true.B
  if_id.io.flush := false.B


  /////////////////////////////////////////////////////////////////////////////
  // ID STAGE
  /////////////////////////////////////////////////////////////////////////////

  // Send opcode to control

  // Grab rs1 and rs2 from the instruction in this stage

  // (Part III and/or Part IV) Send inputs from this stage to the hazard detection unit

  // Send rs1 and rs2 to the register file

  // Send the instruction to the immediate generator

  // Sending signals from this stage to EX stage
  //  - Fill in the ID_EX register
  //  - Set the execution control singals
  //  - Set the memory control singals
  //  - Set the writeback control signals

  // (Part III and/or Part IV) Set the control signals on the ID_EX pipeline register
  id_ex.io.valid := true.B
  id_ex.io.flush := false.B
  id_ex_ctrl.io.valid := true.B
  id_ex_ctrl.io.flush := false.B


  /////////////////////////////////////////////////////////////////////////////
  // EX STAGE
  /////////////////////////////////////////////////////////////////////////////

  // (Skip for Part I) Set the inputs to the hazard detection unit from this stage

  // (Skip for Part I) Set the inputs to the forwarding unit from this stage

  // Connect the ALU Control wires

  // Connect the ControlTransferUnit control wire

  // (Skip for Part I) Insert the mux for Selecting data to forward from the MEM stage to the EX stage
  //                   (Can send either alu result or immediate from MEM stage to EX stage)

  // (Skip for Part I) Insert the forward operand1 mux

  // (Skip for Part I) Insert the forward operand2 mux

  // Operand1 mux

  // Operand2 mux

  // Set the ALU operation

  // Connect the ALU data wires

  // Connect the ControlTransfer data wires

  // Sending signals from this stage to MEM stage
  //  - Fill in the EX_MEM register
  //  - Set the memory control singals
  //  - Set the writeback control signals

  // (Part III and/or Part IV) Set the control signals on the EX_MEM pipeline register
  ex_mem.io.valid      := true.B
  ex_mem.io.flush      := false.B
  ex_mem_ctrl.io.valid := true.B
  ex_mem_ctrl.io.flush := false.B

  /////////////////////////////////////////////////////////////////////////////
  // MEM STAGE
  /////////////////////////////////////////////////////////////////////////////

  // Set data memory IO

  // Send next_pc back to the Fetch stage

  // (Skip for Part I) Send input signals to the hazard detection unit

  // (Skip for Part I) Send input signals to the forwarding unit

  // Sending signals from this stage to the WB stage
  //  - Fill in the MEM_WB register
  //  - Set the writeback control signals

  // Set the control signals on the MEM_WB pipeline register
  mem_wb.io.valid      := true.B
  mem_wb.io.flush      := false.B
  mem_wb_ctrl.io.valid := true.B
  mem_wb_ctrl.io.flush := false.B


  /////////////////////////////////////////////////////////////////////////////
  // WB STAGE
  /////////////////////////////////////////////////////////////////////////////

  // Set the register to be written to

  // Set the writeback data mux

  // Write the data to the register file

  // (Skip for Part I) Set the input signals for the forwarding unit
}

/*
 * Object to make it easier to print information about the CPU
 */
object PipelinedCPUInfo {
  def getModules(): List[String] = {
    List(
      "imem",
      "dmem",
      "control",
      //"branchCtrl",
      "registers",
      "aluControl",
      "alu",
      "immGen",
      "pcPlusFour",
      //"branchAdd",
      "controlTransfer",
      "forwarding",
      "hazard",
    )
  }
  def getPipelineRegs(): List[String] = {
    List(
      "if_id",
      "id_ex",
      "id_ex_ctrl",
      "ex_mem",
      "ex_mem_ctrl",
      "mem_wb",
      "mem_wb_ctrl"
    )
  }
}
