package com.noteinweb.paas.cloud.aws

import com.noteinweb.paas.cloud.Resource
import org.scalatest.{FlatSpec, Matchers}

class AWSCloudTransformerSpec extends FlatSpec with Matchers {

  it should "deploy S3 bucket" in {
    val bucketResource = Resource("attachments", "attachment", "bucket", Map.empty, List.empty)
    val deployOutput = new AWSCloudTransformer().deploy(List(bucketResource))
    assert(deployOutput == List("NoteWeb_Context_aws_resources"))
  }
}
