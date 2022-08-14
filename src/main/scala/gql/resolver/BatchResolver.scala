package gql.resolver

import cats.implicits._
import cats._
import cats.effect._

/*
 * I input type
 * K the batching key
 * A the output type
 * T the intermediate type which is converted to A
 */
final case class BatchResolver[F[_], I, K, A, T](
    batcher: BatcherReference[K, T],
    partition: I => F[Batch[F, K, A, T]]
) extends LeafResolver[F, I, A] {
  def flatMapF[B](f: A => F[B])(implicit F: FlatMap[F]) =
    BatchResolver(batcher, partition.andThen(_.map(_.flatMapF(f))))

  def contramap[B](g: B => I): BatchResolver[F, B, K, A, T] =
    BatchResolver(batcher, g.andThen(partition))

  def mapK[G[_]: MonadCancelThrow](fk: F ~> G): BatchResolver[G, I, K, A, T] =
    BatchResolver(batcher, partition.andThen(fa => fk(fa).map(bp => bp.copy(post = bp.post.andThen(fk.apply)))))
}
