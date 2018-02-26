create or replace view v_user_label_weights as
SELECT
    u.uuid AS user_uuid,
    u.email AS user_email,
    t.uuid AS tag_uuid,
    t.name AS tag_name,
    t.hex_colour AS tag_colour,
    t.priority,
    ut.weight
FROM
    user u
        LEFT JOIN
    tag t ON 1 = 1
        LEFT JOIN
    user_tag ut ON u.uuid = ut.user_uuid
        AND t.uuid = ut.tag_uuid
ORDER BY user_uuid ASC , tag_uuid ASC;