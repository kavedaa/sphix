name := "sphix"

organization := "org.sphix"

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.5"

crossScalaVersions := Seq("2.10.4", "2.11.1")

libraryDependencies += "org.controlsfx" % "controlsfx" % "8.0.2"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.1" % "test"

scalacOptions in (Compile,doc) ++= Seq("-diagrams")
