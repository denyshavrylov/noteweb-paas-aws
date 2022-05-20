package com.noteinweb.paas.cloud.aws

import com.noteinweb.paas.cloud.CloudTypes.{AWS, CloudType}
import com.noteinweb.paas.cloud.act.{CloudActor, Request, Response}

class AwsCloudActor extends CloudActor {
  override def process(request: Request): Response = {
    Response()
  }

  override def getCloudType: CloudType = AWS
}
