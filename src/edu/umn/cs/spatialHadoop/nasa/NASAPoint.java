package edu.umn.cs.spatialHadoop.nasa;

import java.awt.Color;
import java.awt.Graphics;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Text;

import edu.umn.cs.spatialHadoop.core.Point;
import edu.umn.cs.spatialHadoop.core.Rectangle;
import edu.umn.cs.spatialHadoop.io.TextSerializerHelper;

public class NASAPoint extends Point implements NASAShape {
  
  private static final byte[] Separator = {','};
  
  /**Value stored at this point*/
  public int value;
  
  public NASAPoint() {
  }

  public NASAPoint(double x, double y, int value) {
    super(x, y);
    this.value = value;
  }

  public int getValue() {
    return value;
  }
  
  public void setValue(int value) {
    this.value = value;
  }

  @Override
  public void write(DataOutput out) throws IOException {
    super.write(out);
    out.writeInt(value);
  }
  
  @Override
  public void readFields(DataInput in) throws IOException {
    super.readFields(in);
    this.value = in.readInt();
  }
  
  @Override
  public Text toText(Text text) {
    super.toText(text);
    text.append(Separator, 0, Separator.length);
    TextSerializerHelper.serializeInt(value, text, '\0');
    return text;
  }
  
  @Override
  public void fromText(Text text) {
    super.fromText(text);
    byte[] bytes = text.getBytes();
    text.set(bytes, 1, text.getLength() - 1);
    value = TextSerializerHelper.consumeInt(text, '\0');
  }
  
  @Override
  public String toString() {
    return super.toString() + " - "+value;
  }
  
  /**Valid range of values. Used for drawing.*/
  public static float minValue, maxValue;
  
  public static final float MaxHue;
  static {
    float[] hsbvals = new float[3];
    Color.RGBtoHSB(0, 0, 255, hsbvals);
    MaxHue = hsbvals[0];
  }
  
  @Override
  public void draw(Graphics g, Rectangle fileMBR, int imageWidth,
      int imageHeight, boolean vflip, double scale) {
    Color color;
    if (value < minValue) {
      color = Color.getHSBColor(MaxHue, 0.5f, 1.0f);
    } else if (value < maxValue) {
      float ratio = MaxHue - MaxHue * (value - minValue) / (maxValue - minValue);
      color = Color.getHSBColor(ratio, 0.5f, 1.0f);
    } else {
      color = Color.getHSBColor(0.0f, 0.5f, 1.0f);
    }
    g.setColor(color);
    super.draw(g, fileMBR, imageWidth, imageHeight, vflip, scale);
  }
}
