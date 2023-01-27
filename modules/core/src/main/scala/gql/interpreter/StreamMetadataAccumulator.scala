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
package gql.interpreter

import cats.effect._
import cats.implicits._
import fs2.Stream

trait StreamMetadataAccumulator[F[_], A] {
  def add[B](context: A, stream: Stream[F, B]): F[(Unique.Token, Either[Throwable, B])]

  def getState: F[Map[Unique.Token, A]]
}

object StreamMetadataAccumulator {
  def apply[F[_], A](implicit streamSup: StreamSupervisor[F], F: Concurrent[F]) =
    F.ref(Map.empty[Unique.Token, A]).map { state =>
      new StreamMetadataAccumulator[F, A] {
        override def add[B](context: A, stream: Stream[F, B]): F[(Unique.Token, Either[Throwable, B])] =
          streamSup
            .acquireAwait[B](stream)
            .flatTap { case (token, _) => state.update(_ + (token -> context)) }

        override def getState: F[Map[Unique.Token, A]] = state.get
      }
    }
}
