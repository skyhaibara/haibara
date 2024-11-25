// 评论区组件

import { Avatar, Box, Button, Card, Divider, Grid, TextField, Typography, IconButton, Tooltip } from "@mui/material";
import DeleteIcon from "@mui/icons-material/Delete";
import { useEffect, useState } from "react";
import { api } from "@/utils/axios.ts";
import { Comment } from "@/models/comment.ts";
import useAuthStore from "@/stores/auth.ts";


function MyComment({ comment, firstName, created_at, onDelete, commentId }: {
    comment?: string; firstName?: string; created_at?: number
    onDelete?: (commentId: number) => void;
    commentId: number;
}) {
    const date = new Date(Number(created_at) * 1000);
    return (
        <Grid container sx={{ backgroundColor: "#f0f0f02d" }}>
            <Grid item xs={12} display="flex" alignItems="center" sx={{ margin: 2 }}>
                <Avatar sx={{ marginRight: 4 }}>{firstName?.slice(0, 4)}</Avatar>
                <Box flexGrow={1} maxWidth="80%" overflow="hidden" textOverflow="ellipsis" whiteSpace="nowrap">
                    <Typography variant="body1" noWrap>{comment}</Typography>
                </Box>
                <Typography variant="caption" sx={{ flexGrow: 0, marginLeft: 1 }}>{date.toLocaleString()}</Typography>
                {onDelete && (
                    <Tooltip title="Delete">
                        <IconButton onClick={() => onDelete(commentId)}>
                            <DeleteIcon />
                        </IconButton>
                    </Tooltip>
                )}
            </Grid>
        </Grid>
    );
}

export default function Comments({ id }: { id: number }) {

    const [comments, setComments] = useState<Array<Comment>>();
    const [commentSend, setCommentSend] = useState<string>();
    const authStore = useAuthStore();

    function fetchComments() {
        api().get(`/articles/${id}/comments`).then(
            (res) => {
                const r = res.data;
                setComments(r.data);
            },
        );
    }

    function deleteComment(commentId: number) {
        api().delete(`/comments/${commentId}`).then(() => {
            fetchComments();
        }).catch((error) => {
            console.error("Error deleting comment:", error);
        });
    }

    useEffect(() => {
        fetchComments();
    }, []);

    function submitComment() {
        api().post("/comments", {
            article_id: id,
            content: commentSend,
            user_id: authStore?.user?.id,
        }).then(() => {
            fetchComments();
            setCommentSend("");
        });
    }

    return (
        <>
            <Divider>评论区</Divider>
            <Card sx={{ marginTop: 3 }}>
                <Box sx={{
                    display: "flex",
                    justifyContent: "space-between",
                    margin: "1rem 0",
                    gap: "1rem",
                }}>
                    <TextField
                        maxRows="20"
                        minRows="1"
                        multiline
                        fullWidth
                        value={commentSend}
                        onChange={(e) => setCommentSend(e.target.value)}
                    />
                    <Button variant="contained" onClick={submitComment}>评论</Button>
                </Box>
                <Grid container spacing={2}>
                    {comments?.map((oneOfComments, index) => {
                        return (
                            <Grid item xs={12} key={index}>
                                <MyComment
                                    comment={oneOfComments.content}
                                    firstName={oneOfComments.user?.username}
                                    created_at={oneOfComments.created_at}
                                    onDelete={deleteComment}
                                    commentId={oneOfComments.id}
                                />
                            </Grid>
                        );
                    })}
                </Grid>
            </Card>
        </>
    );
}
