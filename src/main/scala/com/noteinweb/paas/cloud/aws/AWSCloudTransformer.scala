package com.noteinweb.paas.cloud.aws;

import com.noteinweb.paas.cloud.CloudTypes.{AWS, CloudType}
import com.noteinweb.paas.cloud.Resource
import com.noteinweb.paas.cloud.transform.CloudTransformer

import scala.collection.immutable.List;

class AWSCloudTransformer extends CloudTransformer {
    override def getCloudType: CloudType = AWS

    override def deploy(resources: List[Resource]): List[String] = {
        deployResourcesWithSdk(resources).map(resource => resource.toString).toList
    }

    override def undeploy(resources: List[Resource]): List[String] = {
        List()
    }

    private def deployResourcesWithCdktf(resources: List[Resource]) = {
        AstTerraformStack.deploy(resources)
    }

    private def deployResourcesWithSdk(resources: List[Resource]) = {
        AstSdkStack.deploy(resources)
    }
}
