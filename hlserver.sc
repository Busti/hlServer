import java.net.{ DatagramSocket, DatagramPacket, InetAddress }
import java.lang.Thread

println("Starting HLServer")

val socket = new DatagramSocket()
val strips = List(
  (InetAddress.getByName("10.0.0.204"), 135)
)

def write(address: InetAddress, data: Array[Byte]) {
  val packet = new DatagramPacket(data, data.length, address, 1234)
  socket.send(packet)
}

var times = 0

while (true) {
  var map = Map[Tuple2[InetAddress, Int], Array[Byte]]()
  for (strip <- strips) {
    val data = (0 until strip._2 / 3).flatMap(effect(_)).toArray
    map += strip -> data
  }

  for ((k, v) <- map) {
    write(k._1, v)
  }

  times += 1
  
  Thread.sleep(15)
}

def effect(index: Int): Array[Byte] = {
  val pos = (index.toFloat / 45 + times.toFloat / 200f) % 1f
  hsv(pos, 1, 0.02f)
}

def hsv(h: Float, s: Float, v: Float): Array[Byte] = {
  val i = Math.floor(h * 6f).toFloat
  val f = h * 6f - i
  val p = v * (1 - s)
  val q = v * (1 - f * s)
  val t = v * (1 - (1 - f) * s)

  val (r, g, b) = i % 6 match {
      case 0 => (v, t, p)
      case 1 => (q, v, p)
      case 2 => (p, v, t)
      case 3 => (p, q, v)
      case 4 => (t, p, v)
      case 5 => (v, p, q)
      case _ => throw new RuntimeException(s"Cannot convert from HSV to RGB ($this)")
  }
 
  Array((g * 255).toByte, (r * 255).toByte, (b * 255).toByte)
}

socket.close()
