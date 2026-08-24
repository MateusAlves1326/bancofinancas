USE bancofinancas;

START TRANSACTION;

-- Credenciais iniciais: LOJA / 1234
INSERT INTO usuarios (username, password, role, cliente_id, agencia_id)
SELECT
    'LOJA',
    '$2b$10$7n0pyx3AACMAAKWkdWdRCehNkLj4VzTKU0zx9WPhJEZ5VcP2a1He.',
    'LOJA',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1 FROM usuarios WHERE username = 'LOJA'
);

-- MASTER usa a role ADMIN, que e a role administrativa existente no projeto.
-- Credenciais iniciais: MASTER / 1234
INSERT INTO usuarios (username, password, role, cliente_id, agencia_id)
SELECT
    'MASTER',
    '$2b$10$7n0pyx3AACMAAKWkdWdRCehNkLj4VzTKU0zx9WPhJEZ5VcP2a1He.',
    'AGENCIA',
    NULL,
    NULL
WHERE NOT EXISTS (
    SELECT 1 FROM usuarios WHERE username = 'MASTER'
);

COMMIT;

SELECT id, username, role, cliente_id, agencia_id
FROM usuarios
WHERE username IN ('LOJA', 'MASTER');
