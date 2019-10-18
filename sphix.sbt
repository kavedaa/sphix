name := "sphix"

organization := "org.sphix"

version := "0.1-SNAPSHOT"

scalaVersion := "2.12.10"

crossScalaVersions := Seq("2.11.12", "2.12.10")

libraryDependencies += "org.controlsfx" % "controlsfx" % "8.40.15"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % "test"

scalacOptions in (Compile,doc) ++= Seq("-diagrams")
