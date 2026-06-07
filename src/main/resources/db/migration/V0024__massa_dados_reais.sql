-- ============================================================
-- V24 - Massa de dados reais de Fornecedores, Materiais e Estoque
-- ============================================================

-- 1. Categorias de Material
INSERT INTO public.categoria_material (codigo, nome) VALUES 
    ('FERRAGENS', 'Ferragens'),
    ('PINTURA', 'Pintura'),
    ('ACABAMENTOS', 'Acabamentos')
ON CONFLICT (codigo) DO NOTHING;

-- 2. Fornecedores
INSERT INTO public.fornecedor (codigo, nome, telefone) VALUES
    ('GERDAU-001', 'Gerdau Aços Longos S.A.', '(11) 4000-1111'),
    ('VOTORANTIM-001', 'Votorantim Cimentos N/NE S.A.', '(11) 4000-2222'),
    ('TIGRE-001', 'Tigre Tubos e Conexões S.A.', '(11) 4000-3333'),
    ('AMANCO-001', 'Amanco Wavin', '(11) 4000-4444'),
    ('SUVINIL-001', 'Suvinil Tintas', '(11) 4000-5555'),
    ('PORTOBELLO-001', 'Portobello S.A.', '(11) 4000-6666')
ON CONFLICT (codigo) DO NOTHING;

-- Vincular fornecedores à Obra 1 (OBR-001)
INSERT INTO public.fornecedor_obra (id_fornecedor, id_obra)
SELECT f.id, o.id 
FROM public.fornecedor f
CROSS JOIN public.obra o
WHERE o.codigo = 'OBR-001'
  AND f.codigo IN ('GERDAU-001', 'VOTORANTIM-001', 'TIGRE-001', 'AMANCO-001', 'SUVINIL-001', 'PORTOBELLO-001')
ON CONFLICT (id_fornecedor, id_obra) DO NOTHING;

-- 3. Materiais
INSERT INTO public.material (id_categoria, id_fornecedor, codigo, nome) VALUES
    (
        (SELECT id FROM public.categoria_material WHERE codigo = 'FERRAGENS'),
        (SELECT id FROM public.fornecedor WHERE codigo = 'GERDAU-001'),
        'MAT-GER-001', 'Vergalhão CA50 10mm (Barra 12m)'
    ),
    (
        (SELECT id FROM public.categoria_material WHERE codigo = 'FERRAGENS'),
        (SELECT id FROM public.fornecedor WHERE codigo = 'GERDAU-001'),
        'MAT-GER-002', 'Vergalhão CA60 5mm (Rolo 100kg)'
    ),
    (
        (SELECT id FROM public.categoria_material WHERE codigo = 'ALVENARIA'),
        (SELECT id FROM public.fornecedor WHERE codigo = 'VOTORANTIM-001'),
        'MAT-VOT-001', 'Cimento CP II-Z 50kg Votorantim'
    ),
    (
        (SELECT id FROM public.categoria_material WHERE codigo = 'ALVENARIA'),
        (SELECT id FROM public.fornecedor WHERE codigo = 'VOTORANTIM-001'),
        'MAT-VOT-002', 'Argamassa ACIII 20kg Votorantim'
    ),
    (
        (SELECT id FROM public.categoria_material WHERE codigo = 'HIDRAULICO'),
        (SELECT id FROM public.fornecedor WHERE codigo = 'TIGRE-001'),
        'MAT-TIG-001', 'Tubo PVC Soldável 25mm (Barra 3m)'
    ),
    (
        (SELECT id FROM public.categoria_material WHERE codigo = 'HIDRAULICO'),
        (SELECT id FROM public.fornecedor WHERE codigo = 'TIGRE-001'),
        'MAT-TIG-002', 'Joelho 90 PVC Soldável 25mm Tigre'
    ),
    (
        (SELECT id FROM public.categoria_material WHERE codigo = 'HIDRAULICO'),
        (SELECT id FROM public.fornecedor WHERE codigo = 'AMANCO-001'),
        'MAT-AMA-001', 'Tubo Esgoto SN 100mm (Barra 6m) Amanco'
    ),
    (
        (SELECT id FROM public.categoria_material WHERE codigo = 'PINTURA'),
        (SELECT id FROM public.fornecedor WHERE codigo = 'SUVINIL-001'),
        'MAT-SUV-001', 'Tinta Acrílica Fosca Branca Neve 18L Suvinil'
    ),
    (
        (SELECT id FROM public.categoria_material WHERE codigo = 'ACABAMENTOS'),
        (SELECT id FROM public.fornecedor WHERE codigo = 'PORTOBELLO-001'),
        'MAT-POR-001', 'Porcelanato Munari Branco 90x90 Portobello'
    )
ON CONFLICT (codigo) DO NOTHING;

-- Vincular os novos materiais à obra OBR-001 na tabela associativa
INSERT INTO public.material_obra (id_material, id_obra)
SELECT m.id, o.id
FROM public.material m
CROSS JOIN public.obra o
WHERE o.codigo = 'OBR-001'
  AND m.codigo IN (
      'MAT-GER-001', 'MAT-GER-002', 'MAT-VOT-001', 'MAT-VOT-002', 
      'MAT-TIG-001', 'MAT-TIG-002', 'MAT-AMA-001', 'MAT-SUV-001', 'MAT-POR-001'
  )
ON CONFLICT (id_material, id_obra) DO NOTHING;

-- 4. Inserir Estoque na Obra OBR-001
INSERT INTO public.estoque_obra (
    id_obra, id_material, id_fornecedor, quantidade_atual, quantidade_minima
)
SELECT
    o.id,
    m.id,
    m.id_fornecedor,
    v.quantidade_atual,
    v.quantidade_minima
FROM public.obra o
CROSS JOIN (VALUES
    ('MAT-GER-001', 500.000, 50.000),
    ('MAT-GER-002', 15.000, 2.000),
    ('MAT-VOT-001', 1200.000, 200.000),
    ('MAT-VOT-002', 400.000, 100.000),
    ('MAT-TIG-001', 300.000, 50.000),
    ('MAT-TIG-002', 1500.000, 150.000),
    ('MAT-AMA-001', 200.000, 20.000),
    ('MAT-SUV-001', 45.000, 10.000),
    ('MAT-POR-001', 350.000, 50.000)
) AS v(codigo_material, quantidade_atual, quantidade_minima)
JOIN public.material m ON m.codigo = v.codigo_material
WHERE o.codigo = 'OBR-001'
ON CONFLICT (id_obra, id_material, id_fornecedor) DO NOTHING;

-- 5. Gerar Movimentações de Entrada para esse Estoque
INSERT INTO public.movimentacao_estoque (
    id_obra, id_material, id_fornecedor, tipo, quantidade, observacao
)
SELECT
    eo.id_obra,
    eo.id_material,
    eo.id_fornecedor,
    'ENTRADA',
    eo.quantidade_atual,
    'Carga inicial de massa de dados reais'
FROM public.estoque_obra eo
JOIN public.obra o ON o.id = eo.id_obra
JOIN public.material m ON m.id = eo.id_material
WHERE o.codigo = 'OBR-001'
  AND m.codigo IN (
      'MAT-GER-001', 'MAT-GER-002', 'MAT-VOT-001', 'MAT-VOT-002', 
      'MAT-TIG-001', 'MAT-TIG-002', 'MAT-AMA-001', 'MAT-SUV-001', 'MAT-POR-001'
  )
  AND NOT EXISTS (
      SELECT 1
      FROM public.movimentacao_estoque me
      WHERE me.id_obra = eo.id_obra
        AND me.id_material = eo.id_material
        AND me.id_fornecedor = eo.id_fornecedor
        AND me.tipo = 'ENTRADA'
        AND me.observacao = 'Carga inicial de massa de dados reais'
  );
