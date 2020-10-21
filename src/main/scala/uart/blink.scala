/*
 * This code is a minimal hardware described in Chisel.
 *
 * Blinking LED: the FPGA version of Hello World
 */

import uart._
import chisel3._
import chisel3.Driver

/**
 * The blinking LED component.
 */

class Hello extends Module {
  val io = IO(new Bundle {
    val led = Output(UInt(1.W))
    val txd = Output(UInt(1.W))
  })
  val tx = Module(new BufferedTx(50000000, 115200))
  io.txd := tx.io.txd

  val CNT_MAX = (50000000 / 2 - 1).U;

  val cntReg = RegInit(0.U(32.W))
  val blkReg = RegInit(0.U(1.W))

  //UART Transmission
  val msg = "Hello World!"
  val text = VecInit(msg.map(_.U))
  val len = msg.length.U

  val cntRegUart = RegInit(0.U(8.W))

  tx.io.channel.bits := text(cntRegUart)
  tx.io.channel.valid := cntRegUart =/= len

  when(tx.io.channel.ready && cntRegUart =/= len) {
    cntRegUart := cntRegUart + 1.U
  }

  cntReg := cntReg + 1.U
  when(cntReg === CNT_MAX) {
    cntReg := 0.U
    blkReg := ~blkReg
    //register für uart setzen
  }
  io.led := blkReg
}

/**
 * An object extending App to generate the Verilog code.
 */
object Hello extends App {
  chisel3.Driver.execute(Array[String](), () => new Hello())
}
