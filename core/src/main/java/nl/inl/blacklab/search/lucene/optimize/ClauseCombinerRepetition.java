package nl.inl.blacklab.search.lucene.optimize;

import nl.inl.blacklab.search.lucene.BLSpanQuery;
import nl.inl.blacklab.search.lucene.SpanQueryRepetition;

/**
 * Recognize adjacent identical clauses and combine them.
 */
class ClauseCombinerRepetition extends ClauseCombiner {

	private static final int PRIORITY = 1;

	@Override
	public int priority(BLSpanQuery left, BLSpanQuery right) {
		if (left.equals(right))
			return 1;
		BLSpanQuery leftCl = left instanceof SpanQueryRepetition ? ((SpanQueryRepetition)left).getClause() : left;
		BLSpanQuery rightCl = right instanceof SpanQueryRepetition ? ((SpanQueryRepetition)right).getClause() : right;
		return leftCl.equals(rightCl) ? PRIORITY : CANNOT_COMBINE;
	}

	@Override
	public BLSpanQuery combine(BLSpanQuery left, BLSpanQuery right) {
		if (!canCombine(left, right))
			throw new UnsupportedOperationException("Cannot combine " + left + " and " + right);
		BLSpanQuery leftCl = left;
		int leftMin = 1, leftMax = 1;
		int rightMin = 1, rightMax = 1;
		if (left instanceof SpanQueryRepetition) {
			SpanQueryRepetition l = ((SpanQueryRepetition)left);
			leftCl = l.getClause();
			leftMin = l.getMinRep();
			leftMax = l.getMaxRep();
		}
		if (right instanceof SpanQueryRepetition) {
			SpanQueryRepetition r = ((SpanQueryRepetition)right);
			rightMin = r.getMinRep();
			rightMax = r.getMaxRep();
		}
		return new SpanQueryRepetition(leftCl, leftMin + rightMin, BLSpanQuery.addRepetitionMaxValues(leftMax, rightMax));
	}
}