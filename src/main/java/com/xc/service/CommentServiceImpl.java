package com.xc.service;

import com.xc.dao.CommentRepository;
import com.xc.po.Comment;
import com.xc.service.interfaces.CommentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public List<Comment> listCommentByBlogId(Long blogId) {
        Sort sort = new Sort("createTime");
        List<Comment> comments = commentRepository.findByBlogIdAndParentCommentNull(blogId, sort);
        return divideComments(comments);
    }

    @Transactional
    @Override
    public Comment saveComment(Comment comment) {
        Long parentCommentId = comment.getParentComment().getId();
        if (parentCommentId != -1) {
            comment.setParentComment(commentRepository.findOne(parentCommentId));
        } else {
            comment.setParentComment(null);
        }
        comment.setCreateTime(new Date());
        return commentRepository.save(comment);
    }

    /**
     * 循环每个顶级的评论节点
     *
     * @param comments
     * @return
     */
    private List<Comment> divideComments(List<Comment> comments) {
        List<Comment> commentsCopy = new ArrayList<>();
        for (Comment comment : comments) {
            Comment c = new Comment();
            BeanUtils.copyProperties(comment, c);
            commentsCopy.add(c);
        }
        //合并评论的各层子代到第一级子代集合中
        mergeSubComments(commentsCopy);
        return commentsCopy;
    }

    /**
     * 合并顶部评论下所以的子级评论
     *
     * @param comments root根节点，blog不为空的对象集合
     * @return
     */
    private void mergeSubComments(List<Comment> comments) {
        for (Comment comment : comments) {
            List<Comment> replies = comment.getReplyComments();
            List<Comment> subComments = new ArrayList<>();
            flattenReplies(replies, subComments);

            //修改顶级节点的reply集合为迭代处理后的集合
            comment.setReplyComments(subComments);
        }
    }

    /**
     * 递归迭代，剥洋葱
     *
     * @param comments 被迭代的对象
     * @return
     */
    private void flattenReplies(List<Comment> comments, List<Comment> subComments) {
        for (Comment comment : comments) {
            subComments.add(comment);
            List<Comment> replies = comment.getReplyComments();
            if (comments.size() > 0) {
                flattenReplies(replies, subComments);
            }
        }
    }
}
