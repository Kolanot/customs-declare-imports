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

package unit.base

import java.util.UUID

import org.joda.time.DateTime
import play.api.http.{ContentTypes, HeaderNames}
import play.api.http.HeaderNames.{ACCEPT, CONTENT_TYPE}
import play.api.mvc.Codec
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.customs.imports.models._
import uk.gov.hmrc.customs.imports.models.ChangeReasonCode
import uk.gov.hmrc.http.NotFoundException
import uk.gov.hmrc.wco.dec.{Declaration => WcoDeclaration, MetaData, Response}
import uk.gov.hmrc.customs.imports.controllers.CustomsHeaderNames._

import scala.util.Random

trait ImportsTestData {
  /*
    The first time an declaration is submitted, we save it with the user's EORI, their LRN (if provided)
    and the conversation ID we received from the customs-declarations API response, generating a timestamp to record
    when this occurred.
   */
  val SIXTEEN = 16
  val EIGHT = 8
  val SEVENTY = 70
  val eori: String = randomString(EIGHT)
  val lrn: String = randomString(SEVENTY)
  val mrn: String = randomString(SIXTEEN)
  val ducr: String = randomString(SIXTEEN)

  val before: Long = System.currentTimeMillis()
  val submission = Submission(eori, lrn, Some(mrn))
  val submissionNoMrn = Submission(eori, lrn, None)
  val conversationId = "58494f63-5749-4a62-8193-0452fdc7263b"
  val conversationId2 = "58494f63-5749-4a62-8193-0452fdc726fa"
  val submissionAction = SubmissionAction(submission.id, conversationId, SubmissionActionType.SUBMISSION)
  val cancelationAction = SubmissionAction(submission.id, conversationId2, SubmissionActionType.CANCELLATION)
  val submissionActionDifferentSubmissionId = SubmissionAction(BSONObjectID.generate(), conversationId, SubmissionActionType.SUBMISSION)

  val seqSubmissions: Seq[Submission] = Seq(submission)

  val now: DateTime = DateTime.now
  val response1: Seq[Response] = Seq(Response(functionCode = Random.nextInt(), functionalReferenceId = Some("123")))
  val response2: Seq[Response] = Seq(Response(functionCode = Random.nextInt(), functionalReferenceId = Some("456")))

  val cancelledDeclaration = Declaration(eori, "LRN1", now.minusDays(5), Some("MRN1"),
    actions = Seq(
      DeclarationAction(now.minusDays(2), SubmissionActionType.SUBMISSION, notifications = Seq(DeclarationNotification(1, randomConversationId, now.minusDays(2)))),
      DeclarationAction(now.minusDays(1), SubmissionActionType.CANCELLATION, notifications = Seq(DeclarationNotification(2, randomConversationId, now.minusDays(1))))))
  val unacknowledgedDeclaration = Declaration(eori, "LRN2", now.minusDays(2), None,
    actions = Seq(DeclarationAction(now.minusDays(2), SubmissionActionType.SUBMISSION)))
  val newlySubmittedDeclaration = Declaration(eori, "LRN3", now, None, Seq.empty)

  val functionCodeACK = 10
  val submissionNotification = SubmissionNotification( functionCodeACK, conversationId)

  val submissionResponse = SubmitDeclarationResponse(eori, conversationId, lrn, Some(mrn))

  val declarantEoriValue: String = "ZZ123456789000"
  val declarantLrnValue: String = "MyLrnValue1234"
  val devclientId = "123786"

  val declarationApiVersion ="1.0"
  val dummyToken = "Bearer BXQ3/Treo4kQCZvVcCqKPlwxRN4RA9Mb5RF8fFxOuwG5WSg+S+Rsp9Nq998Fgg0HeNLXL7NGwEAIzwM6vuA6YYhRQnTRFaBhrp+1w+kVW8g1qHGLYO48QPWuxdM87VMCZqxnCuDoNxVn76vwfgtpNj0+NwfzXV2Zc12L2QGgF9H9KwIkeIPK/mMlBESjue4V]"
  val Valid_X_EORI_IDENTIFIER_HEADER: (String, String) = XEoriIdentifierHeaderName -> declarantEoriValue
  val Valid_LRN_HEADER: (String, String) = XLrnHeaderName -> declarantLrnValue
  val Valid_AUTHORIZATION_HEADER = HeaderNames.AUTHORIZATION -> dummyToken
  val XClientIdHeader: (String, String) = XClientIdName -> devclientId
  val acceptHeader: (String, String) = ACCEPT -> s"application/vnd.hmrc.$declarationApiVersion+xml"
  val contentTypeHeader: (String, String) = CONTENT_TYPE -> ContentTypes.XML(Codec.utf_8)
  val httpException = new NotFoundException("Emulated 404 response from a web call")

  val ValidHeaders: Map[String, String] = Map(
    contentTypeHeader,
    Valid_AUTHORIZATION_HEADER,
    Valid_X_EORI_IDENTIFIER_HEADER,
    Valid_LRN_HEADER
  )

  val ValidAPIResponseHeaders: Map[String, String] = Map(
    XClientIdHeader,
    acceptHeader,
    contentTypeHeader,
    Valid_X_EORI_IDENTIFIER_HEADER
  )

  def randomSubmitDeclaration: MetaData = MetaData(declaration = Option(WcoDeclaration(
    functionalReferenceId = Some(randomString(35))
  )))

  def randomConversationId: String = UUID.randomUUID().toString

  def randomCancellation = Cancellation(mrn = randomString(35), changeReasonCode = ChangeReasonCode.DUPLICATE, description = randomString(50))

  protected def randomString(length: Int): String = Random.alphanumeric.take(length).mkString

  def exampleAcceptNotification(functionCode :String) = <_2:MetaData xmlns:_2="urn:wco:datamodel:WCO:DocumentMetaData-DMS:2">
    <_2:WCODataModelVersionCode>3.6</_2:WCODataModelVersionCode>
    <_2:WCOTypeName>RES</_2:WCOTypeName><_2:ResponsibleCountryCode/>
    <_2:ResponsibleAgencyName/><_2:AgencyAssignedCustomizationCode/>
    <_2:AgencyAssignedCustomizationVersionCode/>
    <_2_1:Response xmlns:_2_1="urn:wco:datamodel:WCO:RES-DMS:2">
      <_2_1:FunctionCode>{functionCode}</_2_1:FunctionCode>
      <_2_1:FunctionalReferenceID>25f244455ba54209989f7fe90e00107a</_2_1:FunctionalReferenceID>
      <_2_1:IssueDateTime>
        <_2_2:DateTimeString formatCode="304" xmlns:_2_2="urn:wco:datamodel:WCO:Response_DS:DMS:2">20190114144531Z</_2_2:DateTimeString>
      </_2_1:IssueDateTime>
      <_2_1:AdditionalInformation>
        <_2_1:StatementCode>smartErrorMsg</_2_1:StatementCode>
        <_2_1:StatementDescription>Value per kilo appears too high for this commodity</_2_1:StatementDescription>
        <_2_1:StatementTypeCode>1</_2_1:StatementTypeCode>
        <_2_1:Pointer>
          <_2_1:SequenceNumeric>1</_2_1:SequenceNumeric>
          <_2_1:DocumentSectionCode>07B</_2_1:DocumentSectionCode>
        </_2_1:Pointer>
        <_2_1:Pointer>
          <_2_1:SequenceNumeric>1</_2_1:SequenceNumeric>
          <_2_1:DocumentSectionCode>53A</_2_1:DocumentSectionCode>
        </_2_1:Pointer>
      </_2_1:AdditionalInformation>
      <_2_1:Error>
        <_2_1:Description>Value per kilo appears too high for this commodity</_2_1:Description>
        <_2_1:ValidationCode>DMS13000</_2_1:ValidationCode>
        <_2_1:Pointer>
          <_2_1:DocumentSectionCode>42A</_2_1:DocumentSectionCode>
        </_2_1:Pointer>
        <_2_1:Pointer>
          <_2_1:DocumentSectionCode>67A</_2_1:DocumentSectionCode>
        </_2_1:Pointer>
        <_2_1:Pointer>
          <_2_1:SequenceNumeric>1</_2_1:SequenceNumeric>
          <_2_1:DocumentSectionCode>68A</_2_1:DocumentSectionCode>
        </_2_1:Pointer>
      </_2_1:Error>
      <_2_1:Declaration>
        <_2_1:AcceptanceDateTime>
          <_2_2:DateTimeString formatCode="304" xmlns:_2_2="urn:wco:datamodel:WCO:Response_DS:DMS:2">20181115010101Z</_2_2:DateTimeString>
        </_2_1:AcceptanceDateTime>
        <_2_1:FunctionalReferenceID>Phil3018</_2_1:FunctionalReferenceID>
        <_2_1:ID>19GB0JGCGQL0MFGVR8</_2_1:ID>
        <_2_1:VersionID>1</_2_1:VersionID>
      </_2_1:Declaration>
    </_2_1:Response>
  </_2:MetaData>

def notificationWithMulitpleResponses = <md:MetaData xmlns:md="urn:wco:datamodel:WCO:DocumentMetaData-DMS:2">
  <md:WCODataModelVersionCode>3.6</md:WCODataModelVersionCode>
  <md:WCOTypeName>RES</md:WCOTypeName>
  <md:ResponsibleCountryCode/>
  <md:ResponsibleAgencyName/>
  <md:AgencyAssignedCustomizationCode/>
  <md:AgencyAssignedCustomizationVersionCode/>
  <resp:Response xmlns:resp="urn:wco:datamodel:WCO:RES-DMS:2">
    <resp:FunctionCode>01</resp:FunctionCode>
    <resp:FunctionalReferenceID>a5b4aeb03a384a5faf1b9afeff5dac97</resp:FunctionalReferenceID>
    <resp:IssueDateTime>
      <_2_2:DateTimeString formatCode="304" xmlns:_2_2="urn:wco:datamodel:WCO:Response_DS:DMS:2">20180119155357Z</_2_2:DateTimeString>
    </resp:IssueDateTime>
    <resp:Declaration>
      <resp:AcceptanceDateTime>
        <_2_2:DateTimeString formatCode="304" xmlns:_2_2="urn:wco:datamodel:WCO:Response_DS:DMS:2">20171001000000Z</_2_2:DateTimeString>
      </resp:AcceptanceDateTime>
      <resp:FunctionalReferenceID>Import_Accepted</resp:FunctionalReferenceID>
      <resp:ID>18GBJCM3USAFD2WD51</resp:ID>
      <resp:VersionID>1</resp:VersionID>
    </resp:Declaration>
  </resp:Response>
  <resp:Response xmlns:resp="urn:wco:datamodel:WCO:RES-DMS:2">
    <resp:FunctionCode>13</resp:FunctionCode>
    <resp:FunctionalReferenceID>d5710483848740849ce7415470c2886a</resp:FunctionalReferenceID>
    <resp:IssueDateTime>
      <_2_2:DateTimeString formatCode="304" xmlns:_2_2="urn:wco:datamodel:WCO:Response_DS:DMS:2">20180119155400Z</_2_2:DateTimeString>
    </resp:IssueDateTime>
    <resp:AppealOffice>
      <resp:ID>GBLBA001</resp:ID>
    </resp:AppealOffice>
    <resp:Bank>
      <resp:ReferenceID>CITIGB2LLON</resp:ReferenceID>
      <resp:ID>GB25CITI08320011963155</resp:ID>
    </resp:Bank>
    <resp:ContactOffice>
      <resp:ID>GBLBA001</resp:ID>
      <resp:Communication>
        <resp:ID>See Developer Hub</resp:ID>
        <resp:TypeCode>EM</resp:TypeCode>
      </resp:Communication>
      <resp:Communication>
        <resp:ID>+441234567891</resp:ID>
        <resp:TypeCode>FX</resp:TypeCode>
      </resp:Communication>
    </resp:ContactOffice>
    <resp:Status>
      <resp:NameCode>4</resp:NameCode>
    </resp:Status>
    <resp:Declaration>
      <resp:FunctionalReferenceID>Import_Accepted</resp:FunctionalReferenceID>
      <resp:ID>18GBJCM3USAFDHGJGJHG1</resp:ID>
      <resp:VersionID>1</resp:VersionID>
      <resp:GoodsShipment>
        <resp:GovernmentAgencyGoodsItem>
          <resp:SequenceNumeric>1</resp:SequenceNumeric>
          <resp:Commodity>
            <resp:DutyTaxFee>
              <resp:DeductAmount>0.00</resp:DeductAmount>
              <resp:DutyRegimeCode>100</resp:DutyRegimeCode>
              <resp:TaxRateNumeric>9.70</resp:TaxRateNumeric>
              <resp:TypeCode>A00</resp:TypeCode>
              <resp:Payment>
                <resp:TaxAssessedAmount>97.00</resp:TaxAssessedAmount>
                <resp:PaymentAmount>97.00</resp:PaymentAmount>
              </resp:Payment>
            </resp:DutyTaxFee>
            <resp:DutyTaxFee>
              <resp:DeductAmount>0.00</resp:DeductAmount>
              <resp:DutyRegimeCode>100</resp:DutyRegimeCode>
              <resp:TaxRateNumeric>20.00</resp:TaxRateNumeric>
              <resp:TypeCode>B00</resp:TypeCode>
              <resp:Payment>
                <resp:TaxAssessedAmount>219.40</resp:TaxAssessedAmount>
                <resp:PaymentAmount>219.40</resp:PaymentAmount>
              </resp:Payment>
            </resp:DutyTaxFee>
          </resp:Commodity>
        </resp:GovernmentAgencyGoodsItem>
      </resp:GoodsShipment>
    </resp:Declaration>
  </resp:Response>
  <resp:Response xmlns:resp="urn:wco:datamodel:WCO:RES-DMS:2">
    <resp:FunctionCode>09</resp:FunctionCode>
    <resp:FunctionalReferenceID>f634f77719d546a7b0eb83092bb3a015</resp:FunctionalReferenceID>
    <resp:IssueDateTime>
      <_2_2:DateTimeString formatCode="304" xmlns:_2_2="urn:wco:datamodel:WCO:Response_DS:DMS:2">20180119155400Z</_2_2:DateTimeString>
    </resp:IssueDateTime>
    <resp:AdditionalInformation>
      <resp:StatementCode>A2</resp:StatementCode>
      <resp:StatementTypeCode>AFB</resp:StatementTypeCode>
    </resp:AdditionalInformation>
    <resp:Declaration>
      <resp:FunctionalReferenceID>Import_Accepted</resp:FunctionalReferenceID>
      <resp:ID>18GBJCM3USADGHDGHD</resp:ID>
      <resp:VersionID>1</resp:VersionID>
    </resp:Declaration>
  </resp:Response>
</md:MetaData>


}
