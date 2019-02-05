/*
 * Copyright 2019 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.customs.imports.models

import play.api.libs.json.{Format, Json, OFormat}
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.mongo.json.ReactiveMongoFormats
import uk.gov.hmrc.mongo.json.ReactiveMongoFormats.mongoEntity

case class Submission( eori: String,
                       localReferenceNumber: String,
                       mrn: Option[String] = None,
                       submittedDateTime: Long = System.currentTimeMillis(),
                       id: BSONObjectID = BSONObjectID.generate() )

object Submission {
  implicit val objectIdFormats: Format[BSONObjectID] = ReactiveMongoFormats.objectIdFormats
  implicit val formats: Format[Submission] = mongoEntity {
    Json.format[Submission]
  }
}

case class SubmissionAction( submissionId : BSONObjectID,
                             conversationId: String,
                             dateTimeSent: Long = System.currentTimeMillis(),
                             id: BSONObjectID = BSONObjectID.generate() )

object SubmissionAction {
  implicit val objectIdFormats: Format[BSONObjectID] = ReactiveMongoFormats.objectIdFormats
  implicit val formats: Format[SubmissionAction] = mongoEntity {
    Json.format[SubmissionAction]
  }
}

case class SubmissionNotification( functionCode: Int,
                                   conversationId: String,
                                   dateTimeIssued: Long = System.currentTimeMillis(),
                                   id: BSONObjectID = BSONObjectID.generate() )

object SubmissionNotification {
  implicit val objectIdFormats: Format[BSONObjectID] = ReactiveMongoFormats.objectIdFormats
  implicit val formats: Format[SubmissionNotification] = mongoEntity {
    Json.format[SubmissionNotification]
  }
}

case class DeclarationAction(dateTimeSent: Long, notifications: Seq[SubmissionNotification] = Seq.empty)

object DeclarationAction {
  implicit val formats: OFormat[DeclarationAction] = Json.format[DeclarationAction]
}

case class Declaration(eori: String,
                       localReferenceNumber: String,
                       submittedDateTime: Long,
                       mrn: Option[String] = None,
                       actions: Seq[DeclarationAction] = Seq.empty)

object Declaration {
  implicit val formats: OFormat[Declaration] = Json.format[Declaration]
}
