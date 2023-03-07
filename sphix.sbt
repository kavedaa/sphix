name := "sphix"

organization := "org.sphix"

version := "0.3.1"

scalaVersion := "3.3.0-RC1"

crossScalaVersions := Seq("2.13.10", "3.2.1")

resolvers += "My Maven Repo Resolver" at "https://mymavenrepo.com/repo/pINely5F8nmLUayJnPul/"

libraryDependencies += "org.controlsfx" % "controlsfx" % "8.40.18"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.14" % "test"

scalacOptions in (Compile,doc) ++= Seq("-diagrams")

publishTo := Some("My Maven Repo Publisher" at "https://mymavenrepo.com/repo/j1YxfckeUitD5ZGTAisl")

