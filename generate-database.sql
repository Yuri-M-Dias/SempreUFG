/* Use essas linhas caso esteja executando da linha de comando(psql)
\timing
\set VERBOSITY verbose
\c teste
*/

CREATE TYPE t_visibilidade_dados AS ENUM  ('Público','Privado', 'Somente Egressos');

CREATE TYPE t_natureza AS ENUM ('Pública', 'Privada', 'Trabalho Autônomo');

CREATE TYPE t_tipo_ies AS ENUM ('Federal', 'Estadual', 'Municipal', 'Particular');

CREATE TYPE t_turno AS ENUM ('Matutino', 'Vespertino', 'Integral');

CREATE TYPE t_item AS ENUM  ('Notícia','Evento', 'Curso', 'Oportunidade de Emprego', 'Diversos');

CREATE TYPE t_regional AS ENUM  ('Goiânia-Câmpus Colemar Natal e Silva', 'Goiânia-Câmpus Samambaria', 'Aparecida de Goiânia', 'Catalão', 'Goiás', 'Jataí');

CREATE TYPE t_nivel_curso AS ENUM  ('Bachalerado', 'Licenciatura', 'Aperfeiçoamento', 'Especialização', 'Mestrado', 'Doutorado');

CREATE TYPE t_programa_academico AS ENUM ('Iniciação Científica', 'Monitoria', 'Extensão', 'Intercambio');

CREATE TABLE LOCALIZACAO_GEOGRAFICA
(
  LOGE_ID						bigserial		PRIMARY KEY,
  LOGE_NOME_CIDADE				varchar(100)		NOT NULL,
  LOGE_NOME_UNIDADE_FEDERATIVA	varchar(100)		NOT NULL,
  LOGE_NOME_PAIS				varchar(100)		NOT NULL,
  LOGE_SIGLA_UNIDADE_FEDERATIVA	varchar(20) 		NOT NULL,
  LOGE_LONGITUDE				float			NOT NULL,
  LOGE_LATITUDE					float			NOT NULL
);

CREATE TABLE EGRESSO
(
  EGRE_ID	bigserial	PRIMARY KEY,
  LOGE_ID	integer 	REFERENCES LOCALIZACAO_GEOGRAFICA NOT   NULL,
  EGRE_NOME					varchar(100)			NOT NULL,
  EGRE_TIPO_DOC_IDENTIDADE		varchar(50)			NOT NULL,
  EGRE_NUMERO_DOC_IDENTIDADE		varchar(50) 			NOT NULL,
  EGRE_DATA_NASCIMENTO 			date 				NOT NULL,
  EGRE_VISIBILIDADE_DADOS 			t_visibilidade_dados	NOT NULL,
  EGRE_DATA_ULTIMA_VISUALIZACAO	date,
  UNIQUE(EGRE_NUMERO_DOC_IDENTIDADE, EGRE_TIPO_DOC_IDENTIDADE)
);

CREATE TABLE ORGANIZACAO
(
  ORGN_ID	bigserial		PRIMARY KEY,
  LOGE_ID	integer		REFERENCES	LOCALIZACAO_GEOGRAFICA,
  ORGN_NOME_RAZAOSOCIAL 	varchar(100)	NOT NULL,
  ORGN_ENDERECO_COMERCIAL 	varchar(300)	NOT NULL,
  ORGN_NATUREZA			 t_natureza	NOT NULL
);

CREATE TABLE AREA_CONHECIMENTO
(
  ARCO_ID			bigserial		PRIMARY KEY,
  ARCO_ARC_ID		integer		REFERENCES AREA_CONHECIMENTO,
  ARCO_NOME_AREA	varchar(300)		UNIQUE NOT NULL,
  ARCO_CODIGO_AREA	numeric(10)		UNIQUE NOT NULL
);

CREATE TABLE ATUACAO
(
  ATUA_ID				bigserial	PRIMARY KEY,
  EGRE_ID				integer	REFERENCES EGRESSO NOT NULL,
  ORGN_ID				integer REFERENCES ORGANIZACAO NOT NULL,
  ARCO_ID				integer	REFERENCES AREA_CONHECIMENTO,
  ATUA_DATA_INICIO			date          		NOT NULL,
  ATUA_DATA_FIM            			date			NOT NULL,
  ATUA_FORMA_INGRESSO		varchar(100) 		NOT NULL,
  ATUA_RENDA_MENSAL_MEDIA	numeric(6,2)		NOT NULL,
  ATUA_SATISFACAO_RENDA		varchar(100)  	NOT NULL,
  ATUA_PERSPECTIVA			varchar(300)		NOT NULL
);

CREATE TABLE UNIDADE_ACADEMICA_UFG
(
  UAFG_ID 				bigserial 		PRIMARY KEY,
  UAFG_REGIONAL 		t_regional		NOT NULL,
  UAFG_NOME 			varchar(500)		UNIQUE NOT NULL
);

CREATE TABLE CURSO_UFG
(
  CUFG_ID 				bigserial 		PRIMARY KEY,
  UAFG_ID		integer REFERENCES UNIDADE_ACADEMICA_UFG NOT NULL,
  ARCO_ID		integer	REFERENCES AREA_CONHECIMENTO NOT NULL,
  CUFG_NOME 			varchar(500)		NOT NULL,
  CUFG_NIVEL 			t_nivel_curso		NOT NULL,
  CUFG_DATA_CRIACAO 		date			NOT NULL,
  CUFG_PRESENCIAL 		boolean 		NOT NULL,
  CURS_TURNO 			t_turno		NOT NULL,
  UNIQUE(CUFG_NOME, UAFG_ID)
);

CREATE TABLE CURSO_OUTRA_IES
(
  COIE_ID 				bigserial 		PRIMARY KEY,
  LOGE_ID			integer REFERENCES LOCALIZACAO_GEOGRAFICA,
  ARCO_ID				integer	REFERENCES AREA_CONHECIMENTO,
  COIE_NOME 			varchar(500)		NOT NULL,
  COIE_NIVEL 			t_nivel_curso		NOT NULL,
  COIE_UNIDADE_ACADEMICA	varchar(500)		NOT NULL,
  COIE_INSTITUICAO		varchar(500)		NOT NULL,
  COIE_TIPO_INSTITUICAO	t_tipo_ies		NOT NULL,
  UNIQUE(COIE_NOME, COIE_TIPO_INSTITUICAO)
);

CREATE TABLE ITEM_DIVULGACAO
(
  ITDI_ID 						bigserial 		PRIMARY KEY,
  ITDI_DATA_REGISTRO				date			UNIQUE,
  ITDI_IDENTIFICADOR_SOLICITANTE		varchar(300)		NOT NULL,
  ITDI_TIPO_ITEM 					t_item			NOT NULL,
  ITDI_ASSUNTO 					varchar(200)		NOT NULL,
  ITDI_DESCRICAO					varchar(500)		NOT NULL,
  ITDI_PARECER 					varchar(500)		NOT NULL,
  ITDI_DATA_DIVULGACAO			date
);

CREATE TABLE ITEM_DIVULGACAO_UNIDADE_ACADEMICA_UFG
(
  IDUA_ID	bigserial 	PRIMARY KEY,
  ITDI_ID	integer 	REFERENCES ITEM_DIVULGACAO		  NOT NULL,
  UAFG_ID	integer 	REFERENCES UNIDADE_ACADEMICA_UFG   NOT NULL
);


CREATE TABLE ITEM_DIVULGACAO_CURSO_UFG
(
  IDCU_ID	bigserial 	PRIMARY KEY,
  ITDI_ID	integer 	REFERENCES ITEM_DIVULGACAO		  NOT NULL,
  CUFG_ID	integer 	REFERENCES CURSO_UFG   NOT NULL
);

CREATE TABLE ITEM_DIVULGACAO_AREA_CONHECIMENTO
(
  IDCU_ID	bigserial 	PRIMARY KEY,
  ITDI_ID	integer 	REFERENCES ITEM_DIVULGACAO		  NOT NULL,
  ARCO_ID	integer	REFERENCES AREA_CONHECIMENTO	  NOT NULL
);

CREATE TABLE HISTORICO_NA_UFG
(
  HIFG_ID				bigserial		PRIMARY KEY,
  EGRE_ID				integer	REFERENCES EGRESSO NOT NULL,
  CURS_ID				integer	REFERENCES CURSO_UFG NOT NULL,
  HIFG_MES_ANO_DE_INICIO 	numeric(6) 		NOT NULL,
  HIFG_MES_ANO_DE_FIM 	numeric(6) 		NOT NULL,
  HIFG_NUMERO_MATRICULA_CURSO 	numeric(10) 		NOT NULL,
  HIFG_TITULO_DO_TRABALHO_FINAL 	varchar(500) 		NOT NULL
);

CREATE TABLE RESIDENCIA
(
  RESI_ID	bigserial 	PRIMARY KEY,
  EGRE_ID	integer 	REFERENCES EGRESSO 			  NOT NULL,
  LOGE_ID	integer 	REFERENCES LOCALIZACAO_GEOGRAFICA NOT NULL,
  RESI_DATA_INICIO 	DATE			UNIQUE,
  RESI_DATA_FIM		DATE			NOT NULL,
  RESI_ENDERECO 	VARCHAR(300)	NOT NULL
);

CREATE TABLE REALIZACAO_DE_PROGRAMA_ACADEMICO
(
  RPAC_ID		bigserial 	PRIMARY KEY,
  HIFG_ID		integer	REFERENCES HISTORICO_NA_UFG NOT NULL,
  RPAC_TIPO 		t_programa_academico		NOT NULL,
  RPAC_DATA_INICIO 	date 					NOT NULL,
  RPAC_DATA_FIM 		date 					NOT NULL,
  RPAC_DESCRICAO 	varchar(500) 				NOT NULL
);

CREATE TABLE AVALIACAO_DO_CURSO_PELO_EGRESSO
(
  ACPE_ID 		bigserial 	PRIMARY KEY,
  HIFG_ID		integer	REFERENCES HISTORICO_NA_UFG NOT NULL,
  ACPE_DATA_AVALIACAO		date			NOT NULL,
  ACPE_SATISFACAO_CURSO		varchar(500)		NOT NULL,
  ACPE_MOTIVACAO_ESCOLHA	varchar(500)		NOT NULL,
  ACPE_CONCEITO_GLOBAL		numeric(2)		NOT NULL,
  ACPE_PREPARACAO_MERCADO 	varchar(500)		NOT NULL,
  ACPE_MELHORIA_CURSO		varchar(500) 		NOT NULL,
  ACPE_ETICA 				varchar(500)		NOT NULL,
  ACPE_HABILIDADES_ESPECIFICAS	varchar(500) 		NOT NULL
);

CREATE TABLE HISTORICO_EM_IEM
(
  HIEM_ID	bigserial	PRIMARY KEY,
  EGRE_ID	integer	REFERENCES EGRESSO NOT NULL,
  LOGE_ID	integer 	REFERENCES LOCALIZACAO_GEOGRAFICA NOT   NULL,
  HIEM_MES_ANO_INICIO	numeric(6) 	NOT NULL,
  HIEM_MES_ANO_FIM 		numeric(6)	NOT NULL
);

CREATE TABLE INSTITUICAO_DE_ENSINO_MEDIO
(
  INEM_ID			bigserial	PRIMARY KEY,
  LOGE_ID			integer 	REFERENCES LOCALIZACAO_GEOGRAFICA,
  INEM_TIPO_DE_IEM 	t_tipo_ies 	NOT NULL
);

CREATE TABLE HISTORICO_OUTRA_IES
(
HOIE_ID 				bigserial 			PRIMARY KEY,
HOIE_MES_ANO_DE_INICIO 	numeric(6) 			NOT NULL,
HOIE_MES_ANO_DE_FIM 	numeric(6)			NOT NULL,
EGRE_ID		integer			REFERENCES EGRESSO NOT NULL,
COIE_ID		integer 	REFERENCES CURSO_OUTRA_IES NOT NULL
);

INSERT INTO public.localizacao_geografica(
    loge_nome_cidade, loge_nome_unidade_federativa, loge_nome_pais,
    loge_sigla_unidade_federativa, loge_longitude, loge_latitude)
VALUES ('Goiânia', 'Goiás', 'Brasil', 'GO', 22.32, 43.21);
INSERT INTO public.localizacao_geografica(
    loge_nome_cidade, loge_nome_unidade_federativa, loge_nome_pais,
    loge_sigla_unidade_federativa, loge_longitude, loge_latitude)
VALUES ('Brasília', 'Distrito Federal', 'Brasil', 'DF', 15.2, 45.24);
INSERT INTO public.localizacao_geografica(
    loge_nome_cidade, loge_nome_unidade_federativa, loge_nome_pais,
    loge_sigla_unidade_federativa, loge_longitude, loge_latitude)
VALUES ('Palmas', 'Tocantins', 'Brasil', 'TO', 8.2, 42.14);

INSERT INTO public.egresso(
    loge_id, egre_nome, egre_tipo_doc_identidade, egre_numero_doc_identidade, egre_data_nascimento, egre_visibilidade_dados, egre_data_ultima_visualizacao)
VALUES (1, 'Fulano de Tal', 'RG', '930945', '1961-06-16', 'Privado', '1962-06-16');
INSERT INTO public.egresso(
    loge_id, egre_nome, egre_tipo_doc_identidade, egre_numero_doc_identidade, egre_data_nascimento, egre_visibilidade_dados, egre_data_ultima_visualizacao)
VALUES (3, 'Sicrano de Tal', 'CPF', '95218835125', '1995-06-08', 'Público', '2015-06-16');
INSERT INTO public.egresso(
    loge_id, egre_nome, egre_tipo_doc_identidade, egre_numero_doc_identidade, egre_data_nascimento, egre_visibilidade_dados, egre_data_ultima_visualizacao)
VALUES (2, 'Beltrano de Tal', 'RG', '233132', '1992-05-08', 'Privado', '2016-02-16');
INSERT INTO public.egresso(
    loge_id, egre_nome, egre_tipo_doc_identidade, egre_numero_doc_identidade, egre_data_nascimento, egre_visibilidade_dados, egre_data_ultima_visualizacao)
VALUES (2, 'Sir Beltrano de Tal', 'CPF', '92483835125', '1988-10-08', 'Público', '2016-01-02');
INSERT INTO public.egresso(
    loge_id, egre_nome, egre_tipo_doc_identidade, egre_numero_doc_identidade, egre_data_nascimento, egre_visibilidade_dados, egre_data_ultima_visualizacao)
VALUES (3, 'Lorde Sicrano', 'CPF', '95218835155', '1995-06-08', 'Público', '2015-06-16');
INSERT INTO public.egresso(
    loge_id, egre_nome, egre_tipo_doc_identidade, egre_numero_doc_identidade, egre_data_nascimento, egre_visibilidade_dados, egre_data_ultima_visualizacao)
VALUES (1, 'Barão Sicrano de Fulanosville', 'CPF', '95213435122', '1998-06-08', 'Privado', '2016-03-24');

INSERT INTO public.area_conhecimento(
    arco_id, arco_nome_area, arco_codigo_area)
VALUES (1, 'Ciências da Saúde ', 40000001);

INSERT INTO public.area_conhecimento(
    arco_id, arco_arc_id, arco_nome_area, arco_codigo_area)
VALUES (2, 1, 'Medicina', 40100006);

INSERT INTO public.area_conhecimento(
    arco_id, arco_arc_id, arco_nome_area, arco_codigo_area)
VALUES (3, 2, 'Clínica Médica', 40101002 );

INSERT INTO public.area_conhecimento(
    arco_id, arco_nome_area, arco_codigo_area)
VALUES (4, 'Engenharia', 40101342 );

INSERT INTO public.area_conhecimento(
    arco_id, arco_nome_area, arco_codigo_area)
VALUES (5, 'História', 40107854 );

INSERT INTO public.organizacao(
    orgn_id, loge_id, orgn_nome_razaosocial, orgn_endereco_comercial,
    orgn_natureza)
VALUES (1, 1, 'Instituto do Software Livre', 'Rua GNU, Número 3423', 'Privada');

INSERT INTO public.organizacao(
    orgn_id, loge_id, orgn_nome_razaosocial, orgn_endereco_comercial,
    orgn_natureza)
VALUES (2, 2, 'Instituto do Software Privado', 'Rua Microsoft, Número 3421', 'Pública');

INSERT INTO public.atuacao(
    atua_id, egre_id, orgn_id, arco_id, atua_data_inicio, atua_data_fim,
    atua_forma_ingresso, atua_renda_mensal_media, atua_satisfacao_renda,
    atua_perspectiva)
VALUES (1, 1, 1, 1, '2013-06-08', '2014-08-09', 'Indicação por amigo', 2323.32, 'Muito satisfeito', 'Profissão que ajuda a salvar o mundo.');

INSERT INTO public.atuacao(
    atua_id, egre_id, orgn_id, arco_id, atua_data_inicio, atua_data_fim,
    atua_forma_ingresso, atua_renda_mensal_media, atua_satisfacao_renda,
    atua_perspectiva)
VALUES (2, 2, 2, 1, '2012-01-08', '2014-08-09', 'Processo seletivo', 5321.32, 'Pouco satisfeito', 'Profissão que permite ganhar muito dinheiro.');

INSERT INTO public.curso_outra_ies(
    coie_id, loge_id, arco_id, coie_nome, coie_nivel, coie_unidade_academica,
    coie_instituicao, coie_tipo_instituicao)
VALUES (1, 3, 1, 'Enfermagem', 'Bachalerado', 'SETEC', 'Alguma IES', 'Federal');

INSERT INTO public.unidade_academica_ufg(
    uafg_id, uafg_regional, uafg_nome)
VALUES (1, 'Goiânia-Câmpus Samambaria', 'Faculdade de Medicina');

INSERT INTO public.unidade_academica_ufg(
    uafg_id, uafg_regional, uafg_nome)
VALUES (2, 'Goiânia-Câmpus Colemar Natal e Silva', 'Faculdade de Engenharia');

INSERT INTO public.unidade_academica_ufg(
    uafg_id, uafg_regional, uafg_nome)
VALUES (3, 'Catalão', 'Faculdade de Hitória');

INSERT INTO public.curso_ufg(
    uafg_id, arco_id, cufg_nome, cufg_nivel, cufg_data_criacao,
    cufg_presencial, curs_turno)
VALUES (1, 3, 'Medicina', 'Bachalerado', '01/04/1960', True, 'Integral');

INSERT INTO public.curso_ufg(
    uafg_id, arco_id, cufg_nome, cufg_nivel, cufg_data_criacao,
    cufg_presencial, curs_turno)
VALUES (3, 4, 'Engenharia da Computação', 'Mestrado', '15/12/1958', True, 'Integral');

INSERT INTO public.curso_ufg(
    uafg_id, arco_id, cufg_nome, cufg_nivel, cufg_data_criacao,
    cufg_presencial, curs_turno)
VALUES (3, 5, 'História', 'Mestrado', '05/06/1982', True, 'Matutino');

INSERT INTO public.historico_em_iem(
    hiem_id, egre_id, loge_id, hiem_mes_ano_inicio, hiem_mes_ano_fim)
VALUES (1, 1, 2, 022008, 122010);

INSERT INTO public.historico_na_ufg(
    egre_id, curs_id, hifg_mes_ano_de_inicio, hifg_mes_ano_de_fim,
    hifg_numero_matricula_curso, hifg_titulo_do_trabalho_final)
VALUES (3, 2, 122013, '122018',
    131564, 'monografia');

INSERT INTO public.historico_na_ufg(
    egre_id, curs_id, hifg_mes_ano_de_inicio, hifg_mes_ano_de_fim,
    hifg_numero_matricula_curso, hifg_titulo_do_trabalho_final)
VALUES (4, 3, 062014, '062018',
    142568, 'Trabalho de Persistencia');

INSERT INTO public.historico_outra_ies(
    hoie_id, hoie_mes_ano_de_inicio, hoie_mes_ano_de_fim, egre_id,
    coie_id)
VALUES (1, 081992, 081995, 6, 1);

INSERT INTO public.instituicao_de_ensino_medio(
    inem_id, loge_id, inem_tipo_de_iem)
VALUES (1, 1, 'Particular');

INSERT INTO public.avaliacao_do_curso_pelo_egresso(
    acpe_id, hifg_id, acpe_data_avaliacao, acpe_satisfacao_curso,
    acpe_motivacao_escolha, acpe_conceito_global, acpe_preparacao_mercado,
    acpe_melhoria_curso, acpe_etica, acpe_habilidades_especificas)
VALUES (1, 1, '2012-01-08', 'Pouco satisfeito: muito trabalho pra fazer', 'Aprendizado', 2, 'Pouca preparação pro mercado.', 'Lembrar os professores que os alunos são humanos também.', 'Falta Ética.', 'Treina pouco as habilidades especificas');

INSERT INTO public.item_divulgacao(
    itdi_id, itdi_data_registro, itdi_identificador_solicitante,
    itdi_tipo_item, itdi_assunto, itdi_descricao, itdi_parecer, itdi_data_divulgacao)
VALUES (1, '2012-01-08', 'Empresário na área de Social Media', 'Notícia', 'Assunto sobre media.', 'Descrição sobre o item.', 'Item será divulgado por patrocínio.', '2012-04-08');

INSERT INTO public.item_divulgacao_area_conhecimento(
    idcu_id, itdi_id, arco_id)
VALUES (1, 1, 1);

INSERT INTO public.item_divulgacao_curso_ufg(
    idcu_id, itdi_id, cufg_id)
VALUES (1, 1, 1);

INSERT INTO public.realizacao_de_programa_academico(
    hifg_id, rpac_tipo, rpac_data_inicio, rpac_data_fim,
    rpac_descricao)
VALUES (1, 'Iniciação Científica', '2011-01-08', '2011-12-12', 'Pesquisa sobre a influência do Virus da Dengue no cotidiano de famílias pobres no interior de Goiás.');

INSERT INTO public.residencia(
    resi_id, egre_id, loge_id, resi_data_inicio, resi_data_fim, resi_endereco)
VALUES (1, 1, 2, '2012-01-08', '2016-04-12', 'Rua da Medicina, Hospital das Dores');

INSERT INTO public.residencia(
    resi_id, egre_id, loge_id, resi_data_inicio, resi_data_fim, resi_endereco)
VALUES (2, 3, 2, '2014-01-08', '2018-04-12', 'Rua da Medicina, Hospital das Dores');

INSERT INTO public.item_divulgacao_unidade_academica_ufg(
    idua_id, itdi_id, uafg_id)
VALUES (1, 1, 1);
