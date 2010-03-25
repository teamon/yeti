package com.yayetee.yeti

import scala.actors.Actor
import scala.actors.Actor._
import scala.collection.mutable.ListBuffer
import java.io.{OutputStream, InputStream}
import gnu.io.{SerialPort, CommPortIdentifier}

class SerialReaderActor(in: InputStream, receiver: Actor) extends Actor {
	protected var buffer = ""

	def act {
		loop {
			reactWithin(1) {
				case SerialMessage.StopActor => exit
			}
			
			val input = in.read
			println("I`m reading!!")
			if (input >= -1) {
				val char = input.toChar
				if (char == '\n') {
					receiver ! SerialMessage.Plain(buffer)
					buffer = ""
				} else {
					buffer += char
				}
			}
		}
	}
}

class SerialWriterActor(out: OutputStream) extends Actor {
	def act {
		loop {
			receive {
				case SerialMessage.StopActor => exit
				case SerialMessage.Plain(msg) => msg.foreach { out.write(_) }
			}
		}
	}
}

abstract class SerialActor extends Actor {
	var reader: SerialReaderActor = _
	var writer: SerialWriterActor = _

	def connect(portName: String){
		val port = Serial.connection(portName)
		port.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_2, SerialPort.PARITY_NONE)

		reader = new SerialReaderActor(port.getInputStream, this)
		writer = new SerialWriterActor(port.getOutputStream)
		reader.start
		writer.start

		start
	}

	def act {
		loop {
			receive {
				case SerialMessage.StopActor =>
					reader !! SerialMessage.StopActor
					writer !! SerialMessage.StopActor
					exit

				case _ => parse(_)
			}
		}
	}

	def parse(x: Any)

}

object SerialMessage {
	case class Plain(msg: String)
	case class StopActor
}

object Serial {

	def portList = {
		val list = new ListBuffer[String]
		val e = CommPortIdentifier.getPortIdentifiers
		while (e.hasMoreElements) {
			list += e.nextElement.asInstanceOf[CommPortIdentifier].getName
		}
		list.toList
	}

	def connection(portName: String) = {
		val portIdentifier = CommPortIdentifier.getPortIdentifier(portName)
		
		if (portIdentifier.isCurrentlyOwned) throw new Exception("Port in use")

		val commPort = portIdentifier.open(this.getClass.getName, 2000)

		if (!commPort.isInstanceOf[SerialPort]) throw new Exception("Serial ports only")

		commPort.asInstanceOf[SerialPort]
	}
}
