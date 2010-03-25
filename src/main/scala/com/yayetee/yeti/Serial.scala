package com.yayetee.yeti

import scala.actors.Actor
import scala.actors.Actor._
import scala.collection.mutable.ListBuffer
import scala.util.matching.Regex
import java.io.{OutputStream, InputStream}
import gnu.io.{SerialPort, CommPortIdentifier}


object SerialMessage {
	case class Plain(msg: String)
	case class Joystick(id: Int, value: Int)

	case class Stop
}

object Serial {
	def portList = {
		val list = new ListBuffer[String]
		val e = CommPortIdentifier.getPortIdentifiers
		while (e.hasMoreElements) {
			list += e.nextElement.asInstanceOf[CommPortIdentifier].getName
		}
		// TODO: Return only availible ports
		list.toList
	}

	def connection(portName: String) = {
		val portIdentifier = CommPortIdentifier.getPortIdentifier(portName)

		if (portIdentifier.isCurrentlyOwned) throw new Exception("Port in use")

		val commPort = portIdentifier.open(this.getClass.getName, 2000)

		if (!commPort.isInstanceOf[SerialPort]) throw new Exception("Serial ports only")

		commPort.asInstanceOf[SerialPort]
	}

	class Writer(out: OutputStream) extends Actor {
		def act {
			loop {
				receive {
					case SerialMessage.Stop => exit
					case SerialMessage.Plain(msg) => msg.foreach {out.write(_)}
				}
			}
		}
	}

	class Reader(in: InputStream, receiver: Actor) extends Actor {
		protected var buffer = ""

		object Regex {
			val Joy = """J(\d+)=(-?)(\d+)""".r
		}

		def act {
			loop {
//				reactWithin(1) {
//					case SerialMessage.Stop => exit
//				}

				val input = in.read
				if (input >= -1) {
					val char = input.toChar
					if (char == '\n') {
						receiver ! (buffer match {
							case Regex.Joy(id, sign, value) =>
								SerialMessage.Joystick(id.toInt, if(sign == "-") -value.toInt else value.toInt)
							case _ =>
								SerialMessage.Plain(buffer)
						})
						buffer = ""
					} else {
						buffer += char
					}
				}
			}
		}
	}

}
