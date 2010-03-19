package com.yayetee.yeti

import java.io.{OutputStream, InputStream}

import scala.swing._
import scala.swing.event._
import scala.collection.mutable.ListBuffer
import gnu.io.{CommPortIdentifier, SerialPort}

class SerialReader(in: InputStream) extends Runnable {
	def run() {
		val buffer = new Array[Byte](1024)
		var len = -1

		try {
			while (true) {
				len = in.read(buffer)
				if (len >= -1) println(new String(buffer, 0, len))
			}
		} catch {
			case _ => println("dupa in")
		}
	}
}

class SerialWriter(out: OutputStream) extends Runnable {
	def run() {
		try {
			out.write(Array('!'.toByte))

			var c = 0
			while (true) {
				c = System.in.read
				if (c >= -1) out.write(c)
			}
		} catch {
			case _ => println("dupa out")
		}
	}
}

class Serial(portName: String) {
	val portIdentifier = CommPortIdentifier.getPortIdentifier(portName)

	if (portIdentifier.isCurrentlyOwned) {
		println("ERROR: Port in use")
	} else {
		val commPort = portIdentifier.open(this.getClass.getName, 2000)

		if (commPort.isInstanceOf[SerialPort]) {
			val serialPort = commPort.asInstanceOf[SerialPort]
			serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_2, SerialPort.PARITY_NONE)

			val in = serialPort.getInputStream
			val out = serialPort.getOutputStream

			new Thread(new SerialReader(in)).start
			new Thread(new SerialWriter(out)).start
		} else {
			println("ERROR: Only serial ports are handled by this example")
		}
	}
}

object Serial {
	def portList = {
		val list = new ListBuffer[String]
		val e = CommPortIdentifier.getPortIdentifiers
		while (e.hasMoreElements) {
			val x: CommPortIdentifier = e.nextElement.asInstanceOf[CommPortIdentifier]
			list += x.getName
		}
		list
	}
}

object Yeti extends SimpleGUIApplication {
	def top = new MainFrame {
		title = "Yeti"

		val ports = new ComboBox[String](Serial.portList)

		val go = new Button {
			text = "Connect"
		}

		val prefs = new FlowPanel {
			contents += ports
			contents += go
		}

		val main = new FlowPanel

		val tabbedpane = new TabbedPane {
			pages += new TabbedPane.Page("Main", main)
			pages += new TabbedPane.Page("Preferences", prefs)
		}

		contents = tabbedpane

		listenTo(ports.selection, go)

		reactions += {
			case ButtonClicked(e) => new Serial(ports.selection.item)
		}
	}
}
