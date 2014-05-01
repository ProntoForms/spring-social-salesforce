package org.springframework.social.salesforce.api;

/**
 * Defines operations for getting analytics info.
 *
 * @author Alexandru Luchian
 */

public interface AnalyticsOperations {
    String reportSummaryWithDetail(String reportId);
}
