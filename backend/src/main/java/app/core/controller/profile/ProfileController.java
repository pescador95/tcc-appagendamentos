package app.core.controller.profile;

import app.agendamento.model.pessoa.HistoricoPessoa;
import app.core.model.DTO.Responses;
import app.core.model.profile.MultiPartFormData;
import app.core.model.profile.Profile;
import app.core.utils.BasicFunctions;
import app.core.utils.Contexto;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jetbrains.annotations.NotNull;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ApplicationScoped
@Transactional
public class ProfileController {
    @ConfigProperty(name = "quarkus.http.body.uploads-directory")
    @Inject
    String directory;

    private Responses responses;

    public Profile findOne(Long id) {

        Profile profile = Profile.findById(id);

        if (BasicFunctions.isEmpty(profile)) {
            throw new RuntimeException("Arquivo não encontrado");
        }

        return profile;
    }

    public Response sendUpload(@NotNull MultiPartFormData file, String fileRefence, Long idHistoricoPessoa)
            throws IOException {

        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        Profile profileCheck = Profile.find("historicopessoaid = ?1", idHistoricoPessoa).firstResult();

        HistoricoPessoa historicoPessoa = HistoricoPessoa.findById(idHistoricoPessoa);

        if (BasicFunctions.isNotEmpty(historicoPessoa)) {
            if (BasicFunctions.isEmpty(profileCheck) || !profileCheck.getOriginalName().equals(file.getFile().fileName())) {
                Profile profile = new Profile();
                List<String> mimetype = Arrays.asList("image/jpg", "image/jpeg", "application/msword",
                        "application/vnd.ms-excel", "application/xml",
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "image/gif",
                        "image/png", "text/plain", "application/vnd.ms-powerpoint", "application/pdf", "text/csv",
                        "document/doc", "document/docx",
                        "application/vnd.openxmlformats-officedocument.presentationml.presentation", "application/zip",
                        "application/vnd.sealed.xls");

                if (!mimetype.contains(file.getFile().contentType())) {
                    throw new IOException(
                            "Tipo de arquivo não suportado. Aceito somente arquivos nos formatos: ppt, pptx, csv, doc, docx, txt, pdf, xlsx, xml, xls, jpg, jpeg, png e zip.");
                }

                if (file.getFile().size() > 1024 * 1024 * 4) {
                    throw new IOException("Arquivo muito grande.");
                }

                String fileName = idHistoricoPessoa + "-" + file.getFile().fileName();

                profile.setOriginalName(file.getFile().fileName());

                profile.setKeyName(fileName);

                profile.setMimetype(file.getFile().contentType());

                profile.setFileSize(file.getFile().size());

                profile.setDataCriado(Contexto.dataHoraContexto());

                profile.setHistoricoPessoa(historicoPessoa);

                profile.setNomeCliente(profile.getHistoricoPessoa().getPessoa().getNome());

                profile.setFileReference(fileRefence);

                profile.persist();

                Files.copy(file.getFile().filePath(), Paths.get(directory + fileName));

                responses.setStatus(200);
                responses.getMessages().add("Arquivo adicionado com sucesso!");
            } else {

                responses.setStatus(400);
                responses.getMessages().add("Já existe um arquivo com o mesmo nome. Verifique!");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        } else {

            responses.setStatus(400);
            responses.getMessages().add("Por favor, verifique o Histórico da Pessoa do anexo.");
        }
        return Response.ok(responses).status(responses.getStatus()).build();
    }

    public Response removeUpload(List<Long> pListIdProfile) {

        List<Profile> profiles;
        responses = new Responses();
        responses.setMessages(new ArrayList<>());
        profiles = Profile.list("id in ?1 and ativo = true", pListIdProfile);
        int count = profiles.size();

        try {
            profiles.forEach((profile) -> {

                try {
                    Profile.delete("id = ?1", profile.getId());
                    Files.deleteIfExists(Paths.get(directory + profile.getKeyName()));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });

            responses.setStatus(200);
            if (count <= 1) {
                responses.getMessages().add("Arquivo excluído com sucesso!");
            } else {
                responses.getMessages().add(count + " Arquivos excluídos com sucesso!");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        } catch (Exception e) {

            responses.setStatus(400);
            if (count <= 1) {
                responses.getMessages().add("Arquivo não localizado ou já excluído.");
            } else {
                responses.getMessages().add("Arquivos não localizados ou já excluídos.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }
}