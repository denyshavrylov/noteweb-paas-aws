
name := "noteweb-paas-aws"
organization := "com.noteinweb"

version := "1.0-SNAPSHOT"

scalaVersion := "2.13.8"

lazy val awsSdkVersion = "2.17.181"

libraryDependencies ++= Seq(
  guice,
  "com.noteinweb" %% "noteweb-paas-dsl" % "1.0-SNAPSHOT",

  "software.amazon.awssdk"  % "bom"                   % awsSdkVersion,
  "software.amazon.awssdk"  % "iam"                   % awsSdkVersion,
  "software.amazon.awssdk"  % "s3"                    % awsSdkVersion,
  "software.amazon.awssdk"  % "ec2"                   % awsSdkVersion,
  "software.amazon.awssdk"  % "eks"                   % awsSdkVersion,
  "software.amazon.awssdk"  % "elasticloadbalancing"  % awsSdkVersion,

  "com.hashicorp"           % "cdktf-provider-aws"        % "7.0.29",

  "org.scalatest"           %% "scalatest"                % "3.0.8" % "test",
  "org.scalatestplus"       %% "mockito-4-5"              % "3.2.12.0" % "test"
)