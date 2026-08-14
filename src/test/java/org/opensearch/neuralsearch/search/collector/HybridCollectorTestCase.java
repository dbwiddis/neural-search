/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.opensearch.neuralsearch.search.collector;

import org.apache.lucene.search.LeafCollector;
import org.opensearch.neuralsearch.query.HybridQueryScorer;
import org.opensearch.neuralsearch.query.HybridSubQueryScorer;
import org.opensearch.neuralsearch.query.OpenSearchQueryTestCase;

import java.io.IOException;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

/**
 * Base class for HybridCollector test cases
 */
public class HybridCollectorTestCase extends OpenSearchQueryTestCase {
    /**
     * Collect docs and scores for each sub-query scorer and add them to the leaf collector
     * @param scorer HybridSubQueryScorer object
     * @param scores1 List of scores for the first sub-query
     * @param leafCollector LeafCollector object
     * @param subQueryIndex Index of the sub-query
     * @param docsIds Array of document IDs
     * @throws IOException
     */
    void collectDocsAndScores(
        HybridSubQueryScorer scorer,
        List<Float> scores1,
        LeafCollector leafCollector,
        int subQueryIndex,
        int[] docsIds
    ) throws IOException {
        for (int i = 0; i < docsIds.length; i++) {
            scorer.getSubQueryScores()[subQueryIndex] = scores1.get(i);
            leafCollector.collect(docsIds[i]);
            scorer.resetScores();
        }
    }

    /**
     * Stub the profiler-mode score plumbing of a mocked HybridQueryScorer. In profiler mode the leaf collector
     * asks the HybridQueryScorer to fill the sub-query score array from its verified sub-matches, so a mock has
     * to answer on hybridScores() to make the collector see any score at all.
     * @param mockHybridScorer mocked HybridQueryScorer
     * @param scores score to report for each sub-query, in sub-query order
     * @throws IOException
     */
    void stubHybridScores(HybridQueryScorer mockHybridScorer, float... scores) throws IOException {
        doAnswer(invocation -> {
            float[] subQueryScores = invocation.getArgument(0);
            System.arraycopy(scores, 0, subQueryScores, 0, scores.length);
            return null;
        }).when(mockHybridScorer).hybridScores(any(float[].class));
    }
}
