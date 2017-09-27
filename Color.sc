trait Color {
  def toRGB:  ColorRGB
  def toRGBW: ColorRGBW
  def toHSV:  ColorHSV
}

object Color {
  def apply(red: Float, green: Float, blue: Float): ColorRGB = ColorRGB(red, green, blue)
  def apply(red: Float, green: Float, blue: Float, white: Float): ColorRGBW = ColorRGBW(red, green, blue, white) 
  
  implicit def color2rgb(color: Color) = color.toRGB
  implicit def color2rgbw(color: Color) = color.toRGBW
  implicit def color2hsl(color: Color) = color.toHSV
  
  implicit def color2t_rgb(color: Color) = {
    val rgb = color.toRGB
    (rgb.red, rgb.green, rgb.blue)
  }
  
  implicit def color2t_rgbw(color: Color) = {
    val rgbw = color.toRGBW
    (rgbw.red, rgbw.green, rgbw.blue, rgbw.white)
  }
}

case class ColorRGB(red: Float, green: Float, blue: Float) extends Color {
  require(0f <= red   && red   <= 1f, "Red component is invalid")
  require(0f <= green && green <= 1f, "Green component is invalid")
  require(0f <= blue  && blue  <= 1f, "Blue component is invalid")

  override def toHSV = {

    val max = Set(red, green, blue).max
    val min = Set(red, green, blue).min

    val d = max - min
    val s = if (max == 0) 0 else d / max
    val v = max

    val h = if (max == min) {
      // achromatic
      0f
    } else {
      val h = max match {
        case `red`   => (green - blue)  / d + (if (green < blue) 6 else 0)
        case `green` => (blue  - red)   / d + 2;
        case `blue`  => (red   - green) / d + 4;
      }
      h / 6f
    }

    ColorHSV(h, s, v)
  }

  override def toRGBW = ColorRGBW(red, green, blue, 0f) 

  override def toRGB = this
}

case class ColorRGBW(red: Float, green: Float, blue: Float, white: Float) extends Color {
  require(0f <= red   && red   <= 1f, "Red component is invalid")
  require(0f <= green && green <= 1f, "Green component is invalid")
  require(0f <= blue  && blue  <= 1f, "Blue component is invalid")
  require(0f <= white && white <= 1f, "White component is invalid")

  override def toHSV = {

    val max = Set(red, green, blue).max
    val min = Set(red, green, blue).min

    val d = max - min
    val s = Set(if (max == 0) 0 else d / max, 1f - white).min
    val v = max

    val h = if (max == min) {
      // achromatic
      0f
    } else {
      val h = max match {
        case `red`   => (green - blue)  / d + (if (green < blue) 6 else 0)
        case `green` => (blue  - red)   / d + 2;
        case `blue`  => (red   - green) / d + 4;
      }
      h / 6f
    }

    ColorHSV(h, s, v)
  }

  override def toRGB = ColorRGB(red, green, blue)

  override def toRGBW = this
}

case class ColorHSV(hue: Float, saturation: Float, value: Float) extends Color {
  require(0f <= hue        && hue        <= 1f, "Hue component is invalid")
  require(0f <= saturation && saturation <= 1f, "Saturation component is invalid")
  require(0f <= value      && value      <= 1f, "Value component is invalid")

  override def toHSV = this

  override def toRGB = {
    val h = hue / 360f
    val s = saturation
    val v = value

    val i: Float = Math.floor(h * 6f).toFloat
    val f: Float = hue   * 6f - i
    val p: Float = value * (1 - saturation)
    val q: Float = value * (1 - f * saturation)
    val t: Float = value * (1 - (1 - f) * saturation)

    val (red, green, blue) = i % 6 match {
      case 0 => (v, t, p)
      case 1 => (q, v, p)
      case 2 => (p, v, t)
      case 3 => (p, q, v)
      case 4 => (t, p, v)
      case 5 => (v, p, q)
      case _ => throw new RuntimeException(s"Cannot convert from HSV to RGB ($this)")
    }

    ColorRGB(red, green, blue)
  }

  override def toRGBW = {
    val rgb = toRGB
    ColorRGBW(rgb.red, rgb.green, rgb.blue, value / (1f - saturation))
  }
}
