package views.html.base

import controllers.routes
import play.api.libs.json.Json

import scala.reflect.Selectable.reflectiveSelectable

import lila.app.templating.Environment.{ *, given }
import lila.app.ui.ScalatagsTemplate.{ *, given }
import lila.common.String.html.safeJsonValue

object captcha:

  private val dataCheckUrl = attr("data-check-url")
  private val dataMoves    = attr("data-moves")
  private val dataPlayable = attr("data-playable")

  def apply(form: lila.common.Form.FormLike, captcha: lila.common.Captcha)(using ctx: Context) =
    frag(
      form3.hidden(form("gameId"), captcha.gameId.value.some),
      if ctx.blind then form3.hidden(form("move"), captcha.solutions.head.some)
      else
        val url =
          netBaseUrl + routes.Round.watcher(captcha.gameId.value, captcha.color.name)
        div(
          cls := List(
            "captcha form-group" -> true,
            "is-invalid"         -> lila.common.Captcha.isFailed(form)
          ),
          dataCheckUrl := routes.Main.captchaCheck(captcha.gameId.value)
        )(
          div(cls := "challenge")(
            views.html.board.bits.mini(
              captcha.fen,
              captcha.color
            ) {
              div(
                dataMoves    := safeJsonValue(Json.toJson(captcha.moves)),
                dataPlayable := 1
              )
            }
          ),
          div(cls := "captcha-explanation")(
            label(cls := "form-label")(
              if captcha.color.white then trans.site.whiteCheckmatesInOneMove()
              else trans.site.blackCheckmatesInOneMove()
            ),
            br,
            br,
            trans.site.thisIsAChessCaptcha(),
            br,
            trans.site.clickOnTheBoardToMakeYourMove(),
            br,
            br,
            trans.site.help(),
            " ",
            a(title := trans.site.viewTheSolution.txt(), targetBlank, href := s"${url}#last")(url),
            div(cls := "result success text", dataIcon := licon.Checkmark)(trans.site.checkmate()),
            div(cls := "result failure text", dataIcon := licon.NotAllowed)(trans.site.notACheckmate()),
            form3.hidden(form("move"))
          )
        )
    )

  def hiddenEmpty(form: lila.common.Form.FormLike) = frag(
    form3.hidden(form("gameId")),
    form3.hidden(form("move"))
  )
