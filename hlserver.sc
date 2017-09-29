import java.net.{ DatagramSocket, DatagramPacket, InetAddress }
import java.lang.Thread

import $ivy.`com.github.nscala-time::nscala-time:2.16.0`, com.github.nscala_time.time.Imports._

import $ivy.`org.http4s::http4s-dsl:0.17.2`, $ivy.`org.http4s::http4s-blaze-client:0.17.2`, $ivy.`org.http4s::http4s-blaze-server:0.17.2`
import $ivy.`ch.qos.logback:logback-classic:1.2.3`
import org.http4s._, org.http4s.dsl._
import org.http4s.server.blaze._
import org.http4s.server.syntax._

import $exec.util.color
import $exec.util.util
import $exec.effects.effect
import $exec.effects.effects

println("Starting HLServer")

//Setup sending UDP Socket
val socket = new DatagramSocket()

def write(address: InetAddress, data: Array[Byte]) {
  val packet = new DatagramPacket(data, data.length, address, 1234)
  socket.send(packet)
}

//Setup connected Strips
val strips = List(
  Strip(InetAddress.getByName("10.0.0.204"), 45, 3)
)

//A variable that holds the current effect
var effect = Effects.create("off")

//Start the effect selection service
BlazeBuilder.bindHttp(8080, "0.0.0.0").mountService(
  HttpService {
    case GET -> Root / "effect" / name => {
      effect = Effects.create(name)
      Ok(s"Effect $name started.")
    }
  }, "/"
).run

//The main loop
while (true) {
  val now = DateTime.now
  var map = Map[Strip, Array[Byte]]()
  for (strip <- strips) {
    val data = (0 until strip.length)
      .flatMap(effect.render(_, strip, now))
      .toArray
    map += strip -> data
  }

  for ((k, v) <- map) {
    write(k.address, v)
  }

  Thread.sleep(15)
}
