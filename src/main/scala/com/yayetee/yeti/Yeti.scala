package com.yayetee.yeti

import scala.swing._
import event.{WindowClosing, ButtonClicked}
import javax.swing.UIManager
import gnu.io.SerialPort
import scala.actors.Actor
import scala.actors.Actor._

abstract class App(portName: String) extends Actor {
	// connect to serial port
	val port = Serial.connection(portName)
	port.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_2, SerialPort.PARITY_NONE)


	// set reader and writer actors
	val reader = new Serial.Reader(port.getInputStream, this)
	val writer = new Serial.Writer(port.getOutputStream)
	reader.start
	writer.start

	start

	def act {
		loop {
			receive {
				case SerialMessage.Stop =>
					reader !! SerialMessage.Stop
					writer !! SerialMessage.Stop
					port.close
					exit

				case x:Any => parse(x)
			}
		}
	}

	def title: String

	def parse(x: Any)

	def log(s: String) {
		logTextArea.append(s + "\n")
	}

	lazy val logTextArea = new TextArea {
    rows = 10
		editable = false
	}

	def app = this

	def frame = new Frame {
		title = app.title
		contents = new BoxPanel(Orientation.Vertical){
			contents += gui
			contents += new ScrollPane {
				contents = logTextArea
			}
		}

		reactions += {
			case WindowClosing(_) => app ! SerialMessage.Stop
		}
	}

	def gui: Component
}


object Yeti extends SwingApplication {

	def top = new MainFrame {
		title = "Yeti"

		val btn = new Button { text = "App:Connect" }

		val buttonRefresh = new Button { text = "Refresh" }
		val ports = new ComboBox[String](Serial.portList)

		contents = new BoxPanel(Orientation.Vertical){
			contents += ports
			contents += new BoxPanel(Orientation.Horizontal){
				contents += btn
				contents += buttonRefresh
			}
		}

		listenTo(buttonRefresh, btn)

		reactions += {
			case ButtonClicked(`buttonRefresh`) => ports.peer.setModel(ComboBox.newConstantModel(Serial.portList))
			case ButtonClicked(`btn`) =>
				val app = new Piast(ports.selection.item)
				val frame = app.frame
				frame.pack
				frame.visible = true
		}
	}

	override def startup(args: Array[String]) {
		try {
			UIManager.setLookAndFeel("ch.randelshofer.quaqua.QuaquaLookAndFeel")
		} catch {
			case e => println(e)
		}

		// taken directly from SimpleSwingApplication.scala (scala 2.8.0.Beta1-RC8)
		val t = top
		t.pack
		t.visible = true
	}
}

