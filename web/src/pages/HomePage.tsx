import { useEffect, useState } from "react";
import {
    Box,
    Button,
    IconButton,
    List,
    ListItem,
    Container,
    Typography,
    Paper,
    Divider,
    ListItemText,
    Card,
    CardContent,
    Stack,
    Pagination,
    CircularProgress
} from "@mui/material";
import { Article } from "@/models/article.ts";
import { Create, Add } from "@mui/icons-material";
import { useNavigate } from "react-router-dom";
import useAuthStore from "@/stores/auth.ts";
import { api } from "@/utils/axios.ts";
import useSiteStore from "@/stores/site.ts";

export default function HomePage() {
    const authStore = useAuthStore();
    const siteStore = useSiteStore();
    const navigator = useNavigate();
    const [users, setUsers] = useState<Array<string>>([]);
    const [articles, setArticles] = useState<Array<Article>>([]);
    const [pageSize] = useState(3); // 每页条数（固定为 3）
    const [totalItems, setTotalItems] = useState(0); // 总条数
    const [loading, setLoading] = useState(false);
    const [page, setPage] = useState<number>(1);  // 页码
    const [totalPages, setTotalPages] = useState<number>(0); // 总页数


    useEffect(() => {
        setLoading(true); // 开始加载数据
        api()
            .get("/articles", {
                params: { page: page - 1, size: pageSize } // 后端接口要求 page 从 0 开始
            })
            .then((res) => {
                const r = res.data;
                setArticles(r.data || []);
                let userName = r.data.map((item: any) => item.author.username);
                userName = userName.reverse(); // 反转用户名数组，使得与文章顺序一致
                setUsers(userName);
                setTotalPages(r.totalPages || 0);
                setTotalItems(r.totalItems || 0);
            })
            .finally(() => setLoading(false)); // 数据加载完成
    }, [page, pageSize]);

    return (
        <Container maxWidth="md" sx={{ py: 4 }}>
            {/* 页面标题和新建按钮 */}
            <Stack
                direction="row"
                justifyContent="space-between"
                alignItems="center"
                sx={{ mb: 4 }}
            >
                <Typography
                    variant="h4"
                    align="center"
                    gutterBottom
                    sx={{
                        fontWeight: 600,
                        mb: 4,
                        color: "primary.main",
                    }}
                >
                    所有文章
                </Typography>

                {authStore?.user?.role === "admin" && (
                    <Button
                        variant="contained"
                        startIcon={<Add />}
                        onClick={() => {
                            navigator("/articles/new");
                            siteStore.setCurrentTitle("新建文章");
                        }}
                        sx={{
                            borderRadius: 2,
                            px: 3,
                            py: 1,
                        }}
                    >
                        新建文章
                    </Button>
                )}
            </Stack>

            {/* 文章列表 */}
            <Paper elevation={2} sx={{ borderRadius: 2 }}>
                {loading ? (
                    <Box sx={{ p: 4, textAlign: "center" }}>
                        <CircularProgress />
                    </Box>
                ) : !articles?.length ? (
                    <Box sx={{ p: 4, textAlign: "center" }}>
                        <Typography color="text.secondary" sx={{ mb: 2 }}>
                            还没有写过文章
                        </Typography>
                        <Button
                            variant="outlined"
                            startIcon={<Add />}
                            onClick={() => {
                                navigator("/articles/new");
                                siteStore.setCurrentTitle("新建文章");
                            }}
                        >
                            创建第一篇文章
                        </Button>
                    </Box>
                ) : (
                    <List sx={{ p: 0 }}>
                        {articles?.map((e: Article, index: number) => (
                            <Box key={e.id}>
                                {index > 0 && <Divider />}
                                <ListItem
                                    sx={{
                                        "&:hover": {
                                            bgcolor: "action.hover",
                                        },
                                    }}
                                >
                                    <Card
                                        elevation={0}
                                        sx={{
                                            width: "100%",
                                            bgcolor: "transparent",
                                        }}
                                    >
                                        <CardContent sx={{ p: 2 }}>
                                            <Stack
                                                direction="row"
                                                justifyContent="space-between"
                                                alignItems="center"
                                            >
                                                <Button
                                                    fullWidth
                                                    sx={{
                                                        textAlign: "left",
                                                        textTransform: "none",
                                                    }}
                                                    onClick={() => {
                                                        navigator(`/articles/${e.id}`);
                                                        siteStore.setCurrentTitle(e.title || "");
                                                    }}
                                                >
                                                    <ListItemText
                                                        primary={e.title}
                                                        primaryTypographyProps={{
                                                            variant: "h6",
                                                            color: "text.primary",
                                                        }}
                                                        secondary={new Date(Number(e.created_at) * 1000).toLocaleString()}
                                                    />
                                                    <ListItemText
                                                        secondary={users ? "Write BY: " + users[index] : ""}
                                                    />
                                                </Button>

                                                {authStore?.user?.role === "admin" && (
                                                    <IconButton
                                                        color="primary"
                                                        onClick={() => {
                                                            navigator(`/articles/${e.id}/edit`);
                                                            siteStore.setCurrentTitle("编辑");
                                                        }}
                                                        sx={{
                                                            ml: 2,
                                                            "&:hover": {
                                                                bgcolor: "primary.light",
                                                                color: "primary.contrastText",
                                                            },
                                                        }}
                                                    >
                                                        <Create />
                                                    </IconButton>
                                                )}
                                            </Stack>
                                        </CardContent>
                                    </Card>
                                </ListItem>
                            </Box>
                        ))}
                    </List>
                )}

                {/* 分页控件 */}
                <Box sx={{ display: "flex", justifyContent: "center", mt: 3 }}>
                    <Pagination
                        count={totalPages} // 总页数
                        page={page} // 当前页
                        onChange={(_, value) => setPage(value)} // 页码改变事件，_ 表示忽略 event
                        color="primary"
                        variant="outlined"
                        shape="rounded"
                    />
                </Box>
            </Paper>

            {/* 分页信息 */}
            <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', mt: 2 }}>
                <Typography variant="body2" sx={{ mx: 2 }}>
                    共 {totalItems} 篇文章，当前第 {page} 页 / 共 {totalPages} 页
                </Typography>
            </Box>
        </Container>
    );
}
