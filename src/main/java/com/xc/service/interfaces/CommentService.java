package com.xc.service.interfaces;

import com.xc.po.Comment;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface CommentService {

    List<Comment> listCommentByBlogId(Long blogId);

    Comment saveComment(Comment comment);
}
