/*
 * Copyright 2023 Valdemar Grange
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
package gql.parser

import cats.parse._
import cats._
import cats.data._
import cats.implicits._

object ParserUtil {
  def showExpectation(e: Parser.Expectation): Eval[Chain[String]] = {
    import Parser.Expectation._
    Eval.defer {
      e match {
        case WithContext(contextStr, expect) =>
          showExpectation(expect).map(tl => Chain(s"context: ${contextStr}") ++ tl)
        case Fail(_) => Eval.now(Chain.empty)
        case FailWith(_, message) =>
          Eval.now(Chain(s"message $message"))
        case OneOfStr(_, strs) =>
          Eval.now(Chain(s"one of ${strs.map(x => s"\"$x\"").mkString(" | ")}"))
        case StartOfString(_) =>
          Eval.now(Chain("start of string"))
        case Length(_, expected, actual) =>
          Eval.now(Chain(s"length $expected, but found $actual"))
        case EndOfString(_, length) =>
          Eval.now(Chain(s"end of string but expected length $length"))
        case InRange(_, lower, upper) =>
          Eval.now(
            Chain(
              s"char in range $lower to $upper (code ${lower.toInt} to ${upper.toInt})"
            )
          )
        case ExpectedFailureAt(_, matched) =>
          Eval.now(Chain(s"expected $matched"))
      }
    }
  }

  def showExpectations(es: NonEmptyList[Parser.Expectation]): String =
    Chain.fromSeq(es.toList).flatTraverse(showExpectation).value.mkString_("\nin ")
  // es.traverse(showExpectation).value.mkString_("-" * 10)

  def errorMessage(data: String, e: Parser.Error): String = {
    import scala.io.AnsiColor
    val (msg, conflictingCharacter, ln) = showVirtualTextLine(data, e.failedAtOffset)
    val niceError = showExpectations(e.expected)

    AnsiColor.BLUE +
      s"failed at offset ${e.failedAtOffset} on line $ln with code ${conflictingCharacter.toInt}\n${niceError}\nfor document:\n$msg" +
      AnsiColor.RESET
  }

  def showVirtualTextLine(data: String, offset: Int) = {
    import scala.io.AnsiColor
    val (left, r0) = data.splitAt(offset)
    val right = r0.tail
    val column = left.reverseIterator.takeWhile(_ != '\n').size

    val ln = left.count(_ == '\n')

    val conflictingCharacter = r0.headOption.getOrElse('?')

    val virtualN = 3
    // may be negative
    val virtualLineStartChar = column - virtualN
    val virtualLineIndicators = math.min(virtualLineStartChar + virtualN, virtualN)

    val virtualErrorLine =
      (">" * virtualLineStartChar) + ("^" * (virtualLineIndicators + 1 + virtualN)) + s" line:$ln, column:${column}, offset:${offset}, character code code:${conflictingCharacter.toInt}"

    val green = AnsiColor.RESET + AnsiColor.GREEN

    val n = 400
    val leftChunk = left.takeRight(n)
    val rightChunk = right.take(n)

    val rightChunkThisline = rightChunk.takeWhile(_ != '\n')
    val rightChunkNextLines = rightChunk.dropWhile(_ != '\n')
    val rightChunks =
      green + rightChunkThisline + "\n" +
        AnsiColor.RED + virtualErrorLine +
        green + rightChunkNextLines + AnsiColor.RESET

    val conflictFmt = AnsiColor.RED_B + AnsiColor.BLACK + conflictingCharacter

    val chunk =
      green + leftChunk + conflictFmt + rightChunks

    val err = green + chunk
      .split("\n")
      .map(x => s"| $x")
      .mkString("\n")

    (err, conflictingCharacter, ln)
  }
}
