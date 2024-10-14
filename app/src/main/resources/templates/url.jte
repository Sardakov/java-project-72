@import hexlet.code.dto.url.UrlPage
@import hexlet.code.util.NamedRoutes
@import java.time.format.DateTimeFormatter
@param UrlPage page

@template.layout.page(
page = page,
content = @`
    <section>
        <div class="container-lg mt-5">
            <h1>Сайт: ${page.getUrl().getName()}</h1>
            <table class="table table-bordered table-hover mt-3">
                !{DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");}
                <tbody>
                <tr>
                    <td>ID</td>
                    <td>${page.getUrl().getId()}</td>
                </tr>
                <tr>
                    <td>Имя</td>
                    <td>${page.getUrl().getName()}</td>
                </tr>
                <tr>
                    <td>Дата создания</td>
                    <td>${page.getUrl().getCreatedAt().toLocalDateTime().format(formatter)}</td>
                </tr>
                </tbody>
            </table>
            <h2 class="mt-5">Проверки</h2>
            <form method="post" action="NamedRoutes.checkSite(url.getId())">
                <button type="submit" class="btn btn-primary">Запустить проверку</button>
            </form>
            <table class="table table-border table-hover mt-3">
                <thead>
                <tr>
                    <th class="col-1">ID</th>
                    <th class="col-1">Код ответа</th>
                    <th>title</th>
                    <th>h1</th>
                    <th>description</th>
                    <th class="col-2">Дата проверки</th>
                </tr>
                </thead>
                <tbody>

                @for(var urlCh: page.getUrlCheckList())
                    @if(urlCh.getUrlId() == page.getUrl().getId())
                        <tr>
                            <td>${urlCh.getId()}</td>
                            <td>${urlCh.getStatusCode()}</td>
                            <td>${urlCh.getTitle()}</td>
                            <td>${urlCh.getH1()}</td>
                            <td>${urlCh.getDescription()}</td>
                            <td>${urlCh.getCreatedAt().toLocalDateTime().format(formatter)}</td>
                        </tr>
                    @endif
                @endfor
                </tbody>
            </table>
        </div>
    </section>
`
)