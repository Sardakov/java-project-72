@import hexlet.code.dto.url.UrlsPage
@import hexlet.code.util.NamedRoutes
@import java.time.format.DateTimeFormatter
@param UrlsPage page

@template.layout.page(
page = page,
content = @`
    <section>
        <div class="container-lg mt-5">
            <h1>Сайты</h1>
            <table class="table table-bordered table-hover mt-3">
                <thead>
                <tr>
                    <th class="col-1">ID</th>
                    <th>Имя</th>
                    <th class="col-2">Последняя проверка</th>
                    <th class="col-1">Код ответа</th>
                </tr>
                </thead>
                <tbody>
                !{DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");}
                @for(var url: page.getUrls())
<%--                    !{var urlCheck = page.getChecks().get(url.getId());}--%>
                    <tr>
                        <td>${url.getId()}</td>
                        <td>
                            <a href="${NamedRoutes.showPath(url.getId())}">${url.getName()}</a>
                        </td>
                        <td>
<%--                            ${urlCheck == null ? "" : page.getChecks().get(url.getId()).getCreatedAt().format(formatter)}--%>
                        </td>
                        <td>
<%--                            ${urlCheck == null ? "" : String.valueOf(page.getChecks().get(url.getId()).getStatusCode())}--%>
                        </td>
                    </tr>
                @endfor
                </tbody>
            </table>
        </div>
    </section>
`
)