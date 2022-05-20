package com.noteinweb.paas.cloud.aws

import com.noteinweb.paas.cloud.Resource
import com.typesafe.scalalogging.Logger
import software.amazon.awssdk.awscore.AwsResponse
import software.amazon.awssdk.services.ec2.Ec2Client
import software.amazon.awssdk.services.ec2.model.{CreateVpcRequest, InstanceType, RunInstancesRequest}
import software.amazon.awssdk.services.eks.EksClient
import software.amazon.awssdk.services.elasticloadbalancing.ElasticLoadBalancingClient
import software.amazon.awssdk.services.elasticloadbalancing.model.CreateLoadBalancerRequest
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.CreateBucketRequest


object AstSdkStack {
  private val logger = Logger(getClass)
  private var eksClientOption : Option[EksClient] = Option.empty
  private var s3ClientOption : Option[S3Client] = Option.empty
  private var ec2ClientOption : Option[Ec2Client] = Option.empty
  private var elbClientOption : Option[ElasticLoadBalancingClient] = Option.empty

  def deploy(resources: List[Resource]): Iterable[AwsResponse] = {
    logger.info("Deploying AWS resources: $resources")
    resources.map(resource => resource.resourceType.toLowerCase match {
      case "bucket" => deployS3Bucket(resource)
      case "vm"     => deployVmInstance(resource)
      case "vpc"    => deployVpc(resource)
      case "lb"     => deployLb(resource)
    })
  }

  private def deployLb(resource: Resource): AwsResponse = {
    logger.info("Deploying LB: $resource with ", elbClientOption)
    val createLoadBalancerRequest = CreateLoadBalancerRequest.builder().build()
    val createLbResponse = elbClient.createLoadBalancer(createLoadBalancerRequest)
    createLbResponse
  }

  private def deployVpc(resource: Resource) = {
    logger.info("Deploying VPC: $resource with ", ec2Client)
    val createVpcRequestBuilder = CreateVpcRequest.builder()
    resource.props.get("cidr").map(cidr => createVpcRequestBuilder.cidrBlock(cidr))
    val createVpcResponse = ec2Client.createVpc(createVpcRequestBuilder.build())
    createVpcResponse
  }

  object VmProps extends scala.Enumeration {
    val image, instanceType, keyName, securityGroups = Value
  }

  private def deployVmInstance(resource: Resource) = {
    logger.info("Deploying VM instance: $resource with ", ec2Client)
    val requestBuilder = RunInstancesRequest.builder()
      .minCount(1)
      .maxCount(1)
    applyBuilderProp(resource, VmProps.image, v => requestBuilder.imageId(v) )
    applyBuilderProp(resource, VmProps.instanceType, v => requestBuilder.instanceType(v) )
    applyBuilderProp(resource, VmProps.keyName, v => requestBuilder.keyName(v) )

    val runInstanceResponse = ec2Client.runInstances(requestBuilder.build())
    runInstanceResponse
  }

  private def setupSecurityGroup(resource: Resource, requestBuilder: RunInstancesRequest.Builder): Unit = {
    applyBuilderProp(resource, VmProps.securityGroups, v => requestBuilder.securityGroups(v))
  }

  private def applyBuilderProp(resource: Resource, prop: VmProps.type, f:String => RunInstancesRequest.Builder): Unit = {
    resource.props.get(prop.toString).map(f.apply(_))
  }

  private def deployK8sService(resource: Resource) = {
    logger.info("Deploying Kubernetes service: $resource with ", eksClient)
  }

  private def deployS3Bucket(resource: Resource) = {
    logger.info("Deploying S3 bucket: $resource with ", s3Client)
    val createBucketRequest = CreateBucketRequest.builder()
      .bucket(resource.name)
      .build()
    val createBucketResponse = s3Client.createBucket(createBucketRequest)
    createBucketResponse
  }

  private def s3Client : S3Client = {
    s3ClientOption.getOrElse({
      s3ClientOption = Option(S3Client.builder().build())
      s3ClientOption.get
    })
  }

  private def eksClient : EksClient = {
    eksClientOption.getOrElse({
      eksClientOption = Option(EksClient.builder().build())
      eksClientOption.get
    })
  }

  private def ec2Client : Ec2Client = {
    ec2ClientOption.getOrElse({
      ec2ClientOption = Option(Ec2Client.builder().build())
      ec2ClientOption.get
    })
  }

  private def elbClient : ElasticLoadBalancingClient = {
    elbClientOption.getOrElse({
      elbClientOption = Option(ElasticLoadBalancingClient.builder().build())
      elbClientOption.get
    })
  }
}
