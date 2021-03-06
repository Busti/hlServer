import $exec.effect
import $exec.^.util.color
import $exec.^.util.util
import com.github.nscala_time.time.Imports._

class Rainbow extends Effect {
  override def render(index: Int, strip: Strip, time: DateTime): Color = {
    ColorHSV(index / strip.length.toFloat, 1f, 1f)
  }
}
