CREATE OR REPLACE VIEW v_user_venue_relevance AS
    SELECT
        u.uuid AS user_uuid,
        v.uuid AS venue_uuid,
        COALESCE((SELECT
                        100 * AVG(n_value)
                    FROM
                        v_user_label_n_values
                    WHERE
                        user_uuid = u.uuid
                            AND tag_uuid IN (SELECT
                                tags_uuid
                            FROM
                                venue_tags
                            WHERE
                                venue_uuid = v.uuid)
                            AND n_value > 0),
                0) AS relevance
    FROM
        user u
            LEFT JOIN
        venue v ON 1 = 1;

