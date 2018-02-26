CREATE OR REPLACE VIEW v_all_venue_relevance AS
    SELECT
        vi.user_uuid, vi.relevance, v.*
    FROM
        v_user_venue_relevance vi
            JOIN
        venue v ON v.uuid = vi.venue_uuid;