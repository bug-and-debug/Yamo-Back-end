CREATE OR REPLACE VIEW v_user_label_brackets AS
    SELECT
        user_uuid,
        COALESCE(MIN(weight), 0) AS min_val,
        COALESCE(MAX(weight), 0) AS max_val
    FROM
        v_user_label_weights
    GROUP BY user_uuid;