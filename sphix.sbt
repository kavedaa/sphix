name := "sphix"

organization := "org.sphix"

version := "0.3.2"

scalaVersion := "3.2.2"

crossScalaVersions := Seq("2.13.10", "3.2.2")

libraryDependencies += "org.controlsfx" % "controlsfx" % "8.40.18"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.14" % "test"

scalacOptions in (Compile,doc) ++= Seq("-diagrams")

publishTo := Some("Vedaa Data Public publisher" at "https://mymavenrepo.com/repo/zPAvi2SoOMk6Bj2jtxNA/")

