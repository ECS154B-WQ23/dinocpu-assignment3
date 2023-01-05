// This file is where all of the CPU components are assembled into the whole CPU

package dinocpu.pipelined

import chisel3._
import chisel3.util._
import dinocpu._
import dinocpu.components._
import dinocpu.components.dual._

/**
 * The main CPU definition that hooks up all of the other components.
 *
 * For more information, see section 4.6 of Patterson and Hennessy
 * This follows figure 4.49
 */
class PipelinedDualIssueCPU(implicit val conf: CPUConfig) extends BaseCPU {

}

/*
 * Object to make it easier to print information about the CPU
 */
object PipelinedDualIssueCPUInfo {
  def getModules(): List[String] = {
    List(
      "imem",
      "dmem",
      "pipeA_control",
      "pipeB_control",
      "registers",
      "pipeA_aluControl",
      "pipeB_aluControl",
      "pipeA_alu",
      "pipeB_alu",
      "pipeA_immGen",
      "pipeB_immGen",
      "nextPCmod",
      "forwarding",
      "hazard",
    )
  }
  def getPipelineRegs(): List[String] = {
    List(
      "pipeA_if_id",
      "pipeA_id_ex",
      "pipeA_id_ex_ctrl",
      "pipeA_ex_mem",
      "pipeA_ex_mem_ctrl",
      "pipeA_mem_wb",
      "pipeA_mem_wb_ctrl",
      "pipeB_if_id",
      "pipeB_id_ex",
      "pipeB_id_ex_ctrl",
      "pipeB_ex_mem",
      "pipeB_ex_mem_ctrl",
      "pipeB_mem_wb",
      "pipeB_mem_wb_ctrl"
    )
  }
}
