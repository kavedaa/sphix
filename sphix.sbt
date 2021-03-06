name := "sphix"

organization := "org.sphix"

version := "0.2.2-SNAPSHOT"

scalaVersion := "2.13.4"

resolvers += "My Maven Repo Resolver" at "https://mymavenrepo.com/repo/pINely5F8nmLUayJnPul/"

libraryDependencies += "org.controlsfx" % "controlsfx" % "8.40.15"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % "test"

scalacOptions in (Compile,doc) ++= Seq("-diagrams")

publishTo := Some("My Maven Repo Publisher" at "https://mymavenrepo.com/repo/j1YxfckeUitD5ZGTAisl")

