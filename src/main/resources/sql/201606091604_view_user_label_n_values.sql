CREATE OR REPLACE VIEW v_user_label_n_values AS
    SELECT
    user_uuid,
    user_email,
    tag_uuid,
    tag_name,
    tag_colour,
    priority,
    COALESCE(((weight) - (SELECT
                    min_val
                FROM
                    v_user_label_brackets b
                WHERE
                    b.user_uuid = v.user_uuid)) / ((SELECT
                    max_val
                FROM
                    v_user_label_brackets b
                WHERE
                    b.user_uuid = v.user_uuid) - (SELECT
                    min_val
                FROM
                    v_user_label_brackets b
                WHERE
                    b.user_uuid = v.user_uuid)),
            0) AS n_value
FROM
    v_user_label_weights v;