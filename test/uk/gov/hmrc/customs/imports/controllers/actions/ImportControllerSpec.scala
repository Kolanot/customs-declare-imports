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

package uk.gov.hmrc.customs.imports.controllers.actions

import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.customs.imports.base.{CustomsImportsBaseSpec, ImportsTestData}
import uk.gov.hmrc.customs.imports.models.SubmissionResponse

import scala.concurrent.Future

class ImportControllerSpec extends CustomsImportsBaseSpec with ImportsTestData {
  val uri = "/save-submission-response"
  val jsonBody: JsValue = Json.toJson[SubmissionResponse](submissionResponse)
  val fakeRequest: FakeRequest[JsValue] = FakeRequest("POST", uri).withBody(jsonBody)

  "Auth Action" should {
    "return InsufficientEnrolments when EORI number is missing" in {
      userWithoutEori()

      val result: Future[Result] = route(app, fakeRequest).get

      status(result) must be(UNAUTHORIZED)
      val content = contentAsJson(result)
      content.toString() must be(""""Unauthorized for imports"""")
    }

    "return a failure  when a authorisation fails" in {
      withUnAuthorizedUser()

      val result = route(app, fakeRequest).get

      status(result) must be(UNAUTHORIZED)
      val content = contentAsJson(result)
      content.toString() must be(""""Unauthorized for imports"""")
    }


    "return a success  when a valid request with Enrollments" in {
      withAuthorizedUser()
      withDataSaved(true)

      val result = route(app, fakeRequest).get

      status(result) must be(OK)
    }

    "return an Internal Server Error when there is a problem with the service" in {
      withAuthorizedUser()
      withDataSaved(false)

      val result = route(app, fakeRequest).get

      status(result) must be(INTERNAL_SERVER_ERROR)

      val content = contentAsString(result)
      content.toString() must be("""failed saving submission""")
    }

  }
}
