package com.noteinweb.paas.cloud.aws;

import com.hashicorp.cdktf.providers.aws.AwsProvider
import com.hashicorp.cdktf.providers.aws.ec2.Instance
import com.hashicorp.cdktf.providers.aws.s3.S3Bucket
import com.hashicorp.cdktf.{App, TerraformOutput, TerraformStack}
import com.noteinweb.paas.cloud.Resource
import software.constructs.Construct

class AstTerraformStack(scope: Construct, id: String, resources: List[Resource])
  extends TerraformStack(scope, id) {

  def build {
    AwsProvider.Builder.create(this, "AWS")
      .region("us-east-1")
      .build()
    val outputs: List[TerraformOutput] = resources.flatMap(resource => {
      createResource(resource)
    })
  }

  private def createResource(resource: Resource): Iterable[TerraformOutput] = {
    resource.resourceType.toLowerCase match {
      case "bucket" => createS3Bucket(resource.id)
      case "vminstance" => launchVmInstance(resource.id, "t2.micro", resource.props.get("url").toString)
      case _ => List(TerraformOutput.Builder.create(this, "test").build())
    }
  }

  private def launchVmInstance(id: String, typeAttr: String, url: String): Iterable[TerraformOutput] = {
    val instance = Instance.Builder.create(this, id)
      .ami(url)
      .instanceType(typeAttr)
      .build()
    List(TerraformOutput.Builder.create(this, id + "public_ip")
      .value(instance.getPublicIp())
      .build())
  }

  private def createS3Bucket(id: String): Iterable[TerraformOutput] = {
    val s3Bucket = S3Bucket.Builder.create(this, id)
      .bucket(id)
      .build()
    List(TerraformOutput.Builder.create(this, id + "_output")
      .value(s3Bucket.getId)
      .build())
  }
}

object AstTerraformStack {
  def deploy(resources: List[Resource]): List[String] = {
    val app = new App();
    val stack = new AstTerraformStack(app, "NoteWeb_Context_aws_resources", resources).build
    /* -- unneeded if files stored locally
    new RemoteBackend(stack, RemoteBackendProps.builder()
            .hostname("")
            .organization("")
            .workspaces(new NamedRemoteWorkspace(""))
            .build());
    */

    app.synth();
    List(stack.toString)
  }
}
