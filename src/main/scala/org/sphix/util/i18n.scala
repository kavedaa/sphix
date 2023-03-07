package org.sphix.util

import java.util.Locale
import java.util.ResourceBundle
import scala.util.Try
import java.util.MissingResourceException
import java.text.MessageFormat

trait I18n {

  def basename: String

  private def bundle(locale: Locale) = ResourceBundle getBundle (basename, locale)

  private def value(key: String, locale: Locale) =
    Try { bundle(locale) getString key } getOrElse key

  def i18n(key: String, args: Any*)(implicit locale: Locale) = {
    val text = value(key, locale)
    MessageFormat.format(text, args.map(_.asInstanceOf[AnyRef]): _*)
  }

}
