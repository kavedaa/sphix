package org.sphix.util

import scala.util.Try
import java.text.DateFormat
import java.text.DecimalFormat

class DefaultConverter[T] extends RightConverter[T, T] {
  def convert(a: T) = a
  def deconvert(b: T) = Some(b)
}

object DefaultConverter {
  def apply[T]() = new DefaultConverter[T]
}



object IntConverter extends RightConverter[Int, String] {
  def convert(a: Int) = a.toString
  def deconvert(b: String) = Try(b.toInt).toOption
}

class DoubleConverter extends RightConverter[Double, String] {
  def convert(a: Double) = a.toString
  def deconvert(b: String) = Try(b.toDouble).toOption
}

class FormattedDoubleConverter(dcf: DecimalFormat) extends RightConverter[Double, String] {
  def convert(a: Double) = dcf format a
  def deconvert(b: String) = Try((dcf parse b).doubleValue).toOption
}

object DoubleConverter {
  def apply() = new DoubleConverter
}

object FormattedDoubleConverter {
  def apply(dcf: DecimalFormat) = new FormattedDoubleConverter(dcf)
  def apply(dcfs: String) = new FormattedDoubleConverter(new DecimalFormat(dcfs))
}

class BigDecimalConverter(dcf: DecimalFormat) extends RightConverter[BigDecimal, String] {
  dcf setParseBigDecimal true
  def convert(a: BigDecimal) = dcf format a
  def deconvert(b: String) = Try((dcf parse b).asInstanceOf[java.math.BigDecimal]: BigDecimal).toOption
}

object BigDecimalConverter {
  def apply(dcf: DecimalFormat) = new BigDecimalConverter(dcf)
}

trait DateConverter extends RightConverter[java.util.Date, String] {
  def dateFormat: DateFormat
  def convert(a: java.util.Date) = dateFormat format a
  def deconvert(b: String) = Try(dateFormat parse b).toOption
}

trait DateOptionConverter extends RightConverter[Option[java.util.Date], String] {
  def dateFormat: DateFormat
  def convert(a: Option[java.util.Date]) = a map dateFormat.format getOrElse ""
  def deconvert(b: String) = if (b.isEmpty) Some(None) else Try(Some(dateFormat parse b)).toOption
}