name := "sphix"

organization := "org.sphix"

version := "0.1-SNAPSHOT"

scalaVersion := "2.10.2"

libraryDependencies += "org.controlsfx" % "controlsfx" % "8.0.2"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.0" % "test"

scalacOptions in (Compile,doc) ++= Seq("-diagrams")
