CREATE TABLE material_obra (
    id_material BIGINT NOT NULL,
    id_obra BIGINT NOT NULL,
    PRIMARY KEY (id_material, id_obra),
    FOREIGN KEY (id_material) REFERENCES material(id),
    FOREIGN KEY (id_obra) REFERENCES obra(id)
);

-- Migrar os dados existentes de material.id_obra para a nova tabela
INSERT INTO material_obra (id_material, id_obra)
SELECT id, id_obra FROM material WHERE id_obra IS NOT NULL;

-- Remover a coluna id_obra da tabela material
ALTER TABLE material DROP CONSTRAINT IF EXISTS fk_material_obra;
ALTER TABLE material DROP COLUMN id_obra;
